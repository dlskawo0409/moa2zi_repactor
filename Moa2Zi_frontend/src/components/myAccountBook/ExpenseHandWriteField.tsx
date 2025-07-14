import { useEffect, useState, FormEvent, ReactNode } from "react";
import HandWriteField from "@/components/myAccountBook/HandWriteField";
import SelectableItem from "@/components/myAccountBook/SelectableItem";
import TimePicker from "@/components/myAccountBook/TimePicker";
import CheckIcon from "@/components/svgs/CheckIcon";
import Finance from "@/components/svgs/category/FinanceIcon";
import { Calendar } from "@/components/ui/calendar";
import { DrawerClose } from "@/components/ui/drawer";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { getCategories, getSubCategories } from "@/services/category";
import { postTransactions } from "@/services/transaction";
import { getMemberAccounts, getMemberCards } from "@/services/finance";
import { getBankInfo, getCardIssuerInfo } from "@/utils/getAssetInfo";
import { getCategoryName } from "@/utils/getCategoryName";
import { getCategoryIcon } from "@/utils/getCategoryIcon";
import { formatDateToNumber } from "@/utils/formatDate";
import { formatTime } from "@/utils/formatTime";
import { useUserInfo } from "@/hooks/useUserInfo";
import { Category, SubCategory } from "@/types/category";
import { TransactionMethod } from "@/types/transaction";
import { TimeState } from "@/types/timeState";

interface ExpenseHandWriteFieldProps {
  amount: string;
  handleSave: () => void;
}

const ExpenseHandWriteField = ({ amount, handleSave }: ExpenseHandWriteFieldProps) => {
  const { data } = useUserInfo();
  const [selectedField, setSelectedField] = useState<string | null>(null);
  const [transactionPlace, setTransactionPlace] = useState<string>("");
  const [selectedExpenseCategory, setSelectedExpenseCategory] = useState<Category | null>(null);
  const [selectedExpenseSubCategory, setSelectedExpenseSubCategory] = useState<SubCategory | null>(
    null,
  );
  const [selectedTransactionMethod, setSelectedTransactionMethod] = useState<string | null>("");
  const [includeBudget, setIncludeBudget] = useState<boolean>(false);

  const [expenseCategoryList, setExpenseCategoryList] = useState<Category[]>([]);
  const [expenseSubCategoryList, setExpenseSubCategoryList] = useState<SubCategory[]>([]);
  const [transactionMethodList, setTransactionMethodList] = useState<TransactionMethod[]>([
    { name: "현금", bankCode: "0000" },
  ]);

  const [memo, setMemo] = useState<string>("");
  const [date, setDate] = useState<Date>(new Date());
  const [time, setTime] = useState<TimeState>({ ampm: "오전", hour: "12", minute: "00" });
  const [open, setOpen] = useState<boolean>(false);

  const numericAmount = parseInt(amount.replace(/\D/g, ""), 10) || 0;

  // 입력 필드 클릭 시 해당 필드 선택
  const handleFieldClick = (field: string) => {
    if (selectedField === field) {
      setSelectedField(null);
      return;
    }
    setSelectedField(field);
  };

  // 카테고리 클릭 시 해당 카테고리 선택 후 subCategoryList 불러오기
  const handleExpenseCategoryClick = async (category: Category) => {
    if (selectedExpenseCategory === category) {
      setSelectedExpenseCategory(null);
      return;
    }

    setSelectedExpenseCategory(category);

    try {
      const response = await getSubCategories(category.categoryId, 0, "SPEND");

      setExpenseSubCategoryList(response[0].categoryList ?? []);
    } catch (error) {}
  };

  // 소분류 클릭시 해당 소분류 선택
  const handleExpenseSubCategoryClick = (subCategory: SubCategory) => {
    if (selectedExpenseSubCategory === subCategory) {
      setSelectedExpenseSubCategory(null);
      setExpenseSubCategoryList([]);
      return;
    }

    setSelectedExpenseSubCategory(subCategory);
  };

  // 결제 수단 클릭 시 해당 결제 수단 선택
  const handleTransactionMethodClick = (method: string) => {
    if (selectedTransactionMethod === method) {
      setSelectedTransactionMethod(null);
      return;
    }
    setSelectedTransactionMethod(method);
  };

  // 날짜 선택 시 날짜 선택 창 닫기
  const handleDateChange = (selectedDate: Date | undefined) => {
    if (!selectedDate) {
      setOpen(false);
      return;
    }

    setDate(selectedDate);
    setOpen(false);
  };

  // 예산에 포함시키기
  const toggleIncludeBudget = () => {
    setIncludeBudget((prev) => !prev);
  };

  // 수기 입력 제출
  const handleSubmit = async (event: FormEvent) => {
    // const transactionParams = {
    //   categoryId: selectedExpenseCategory?.categoryId,
    //   transactionDate: formatDateToNumber(date),
    //   transactionBalance: numericAmount,
    //   transactionType: "SPEND",
    //   paymentType: selectedTransactionMethod,
    //   transactionTime: formatTime(time),
    //   merchantName: transactionPlace,
    //   isInBudget: includeBudget,
    //   memo: memo,
    // };

    // console.log(transactionParams);

    if (
      selectedExpenseCategory?.categoryId == null ||
      numericAmount == null ||
      transactionPlace == null
    ) {
      event.preventDefault();
      event.stopPropagation();
      // console.log("수기 입력 실패");
      return;
    }

    try {
      await postTransactions({
        categoryId:
          selectedExpenseSubCategory?.subCategoryId ?? selectedExpenseCategory?.categoryId,
        transactionDate: formatDateToNumber(date),
        transactionBalance: numericAmount,
        transactionType: "SPEND",
        paymentType: selectedTransactionMethod,
        memo: memo,
        transactionTime: formatTime(time),
        merchantName: transactionPlace,
        isInBudget: includeBudget,
      });

      handleSave();
    } catch (error) {
      // console.log("수기 입력 실패");
    }
  };

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await getCategories(null, 0, "SPEND");

        const categories = response.map(
          (category: { categoryId: number; categoryName: string }) => ({
            categoryId: category.categoryId,
            categoryName: getCategoryName(category.categoryName),
          }),
        );

        setExpenseCategoryList(categories);
      } catch (error) {
        // console.error("카테고리를 불러오는 데 실패했습니다:", error);
      }
    };

    const fetchAccounts = async () => {
      try {
        const accounts = await getMemberAccounts();
        const cards = await getMemberCards(data?.memberId!);

        const methodList = [
          { name: "현금" },
          ...accounts.map((account: any) => ({
            name: account.accountName,
            bankCode: account.bankCode,
          })),
          ...cards.map((card: any) => ({
            name: card.cardName,
            cardIssuerCode: card.cardIssuerCode,
          })),
        ];

        // 이름 기준 중복 제거
        const uniqueList = Array.from(
          new Map(methodList.map((item) => [item.name, item])).values(),
        );

        setTransactionMethodList(uniqueList);
      } catch (error) {
        // console.error("계좌 정보를 불러오는 데 실패했습니다:", error);
      }
    };

    fetchCategories();
    fetchAccounts();
  }, [data]);

  return (
    <>
      <div className="overflow-y-auto h-[350px]">
        <HandWriteField
          label="거래처"
          value={transactionPlace}
          type="input"
          isSelected={selectedField === "거래처"}
          onClick={() => handleFieldClick("거래처")}
          onChange={(e) => setTransactionPlace(e.target.value)}
        />
        <HandWriteField
          label="카테고리"
          value={
            selectedExpenseCategory?.categoryName
              ? selectedExpenseCategory.categoryName +
                (selectedExpenseSubCategory?.subCategoryName
                  ? " > " + selectedExpenseSubCategory.subCategoryName
                  : "")
              : selectedExpenseSubCategory?.subCategoryName || ""
          }
          type="text"
          isSelected={selectedField === "지출 카테고리"}
          onClick={() => handleFieldClick("지출 카테고리")}
        />
        <div
          className={`flex flex-col gap-2 transition-all duration-300 ease-in-out overflow-hidden ${
            selectedField === "지출 카테고리" ? "mb-6" : "max-h-0"
          }`}
        >
          {(!expenseSubCategoryList || expenseSubCategoryList.length === 0) && (
            <div className="flex flex-col gap-2">
              {expenseCategoryList.map((category) => {
                const Icon = getCategoryIcon(category.categoryName);

                return (
                  <SelectableItem
                    key={category.categoryId}
                    icon={Icon ? <Icon /> : null}
                    name={category.categoryName}
                    selectedItem={selectedExpenseCategory?.categoryName}
                    onClick={() => handleExpenseCategoryClick(category)}
                  />
                );
              })}
            </div>
          )}
          {expenseSubCategoryList.map((subCategory) => (
            <SelectableItem
              key={subCategory.subCategoryId}
              nonIcon={true}
              name={subCategory.subCategoryName ?? ""}
              selectedItem={selectedExpenseSubCategory?.subCategoryName}
              onClick={() => handleExpenseSubCategoryClick(subCategory)}
            />
          ))}
        </div>
        <HandWriteField
          label="결제 수단"
          value={selectedTransactionMethod}
          type="text"
          isSelected={selectedField === "결제 수단"}
          onClick={() => handleFieldClick("결제 수단")}
        />
        <div
          className={`flex flex-col gap-2 transition-all duration-300 ease-in-out overflow-hidden ${
            selectedField === "결제 수단" ? "max-h-[500px] mb-6" : "max-h-0"
          }`}
        >
          {transactionMethodList.map((method, index) => {
            let iconElement: ReactNode = null;

            if (method.name === "현금") {
              iconElement = <Finance />;
            } else {
              const { Icon: BankIcon } = getBankInfo(method.bankCode ?? "");
              const { Icon: CardIcon } = getCardIssuerInfo(method.cardIssuerCode ?? "");

              const IconComponent = CardIcon || BankIcon;

              if (IconComponent) {
                iconElement = <IconComponent className="w-12 h-12 rounded-full" />;
              }
            }

            return (
              <SelectableItem
                key={index}
                name={method.name}
                selectedItem={selectedTransactionMethod}
                onClick={() => handleTransactionMethodClick(method.name)}
                icon={iconElement}
              />
            );
          })}
        </div>

        <HandWriteField
          label="날짜"
          value={`${date?.toLocaleDateString("ko-KR", {
            year: "numeric",
            month: "long",
            day: "numeric",
          })} ${time.ampm} ${time.hour}:${time.minute}`}
          type="text"
          isSelected={selectedField === "날짜"}
          onClick={() => handleFieldClick("날짜")}
        />
        <div
          className={`flex transition-all duration-500 ease-in-out overflow-hidden px-6 ${
            selectedField === "날짜" ? "max-h-44 mb-6" : "max-h-0"
          }`}
        >
          <Popover open={open} onOpenChange={setOpen}>
            <PopoverTrigger>
              <div className="flex flex-col justify-center w-20">
                <div className="font-bold text-neutral-300">날짜 선택</div>
              </div>
            </PopoverTrigger>
            <PopoverContent side="right">
              <Calendar
                mode="single"
                selected={date}
                onSelect={handleDateChange}
                className="rounded-md border"
              />
            </PopoverContent>
          </Popover>
          <TimePicker
            ampm={time.ampm}
            hour={time.hour}
            minute={time.minute}
            onChange={(ampm, hour, minute) => setTime({ ampm, hour, minute })}
          />
        </div>
        <HandWriteField
          label="메모"
          value={memo}
          type="input"
          isSelected={selectedField === "메모"}
          onClick={() => handleFieldClick("메모")}
          onChange={(e) => setMemo(e.target.value)}
        />
        <div
          className="flex px-6 h-[72px] justify-between items-center border-t-[1px] border-neutral-200 cursor-pointer"
          onClick={toggleIncludeBudget}
        >
          <div className="flex">용돈에 포함시킬래요</div>
          <CheckIcon className={includeBudget ? "text-primary-500" : "text-neutral-300"} />
        </div>
      </div>

      <DrawerClose>
        <div className="w-full px-6">
          <div
            className="bg-primary-500 hover:bg-primary-400 text-white text-sm font-bold rounded-lg transition-colors ease-in-out w-full h-12 flex items-center justify-center cursor-pointer"
            onClick={(e) => handleSubmit(e)}
          >
            추가
          </div>
        </div>
      </DrawerClose>
    </>
  );
};

export default ExpenseHandWriteField;
