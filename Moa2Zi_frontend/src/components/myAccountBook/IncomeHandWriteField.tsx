import { useEffect, useState, FormEvent } from "react";
import HandWriteField from "@/components/myAccountBook/HandWriteField";
import SelectableItem from "@/components/myAccountBook/SelectableItem";
import TimePicker from "@/components/myAccountBook/TimePicker";
import Finance from "@/components/svgs/category/FinanceIcon";
import { Calendar } from "@/components/ui/calendar";
import { DrawerClose } from "@/components/ui/drawer";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { getCategories } from "@/services/category";
import { postTransactions } from "@/services/transaction";
import { getMemberAccounts } from "@/services/finance";
import { getBankInfo } from "@/utils/getAssetInfo";
import { getCategoryName } from "@/utils/getCategoryName";
import { getCategoryIcon } from "@/utils/getCategoryIcon";
import { formatDateToNumber } from "@/utils/formatDate";
import { formatTime } from "@/utils/formatTime";
import { Category } from "@/types/category";
import { TransactionMethod } from "@/types/transaction";
import { TimeState } from "@/types/timeState";

interface IncomeHandWriteFieldProps {
  amount: string;
  handleSave: () => void;
}

const IncomeHandWriteField = ({ amount, handleSave }: IncomeHandWriteFieldProps) => {
  const [selectedField, setSelectedField] = useState<string | null>(null);
  const [transactionPlace, setTransactionPlace] = useState<string>("");
  const [selectedIncomeCategory, setSelectedIncomeCategory] = useState<Category | null>(null);
  const [selectedTransactionMethod, setSelectedTransactionMethod] = useState<string | null>(null);

  const [incomeCategoryList, setIncomeCategoryList] = useState<Category[]>([]);
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

  // 카테고리 클릭 시 해당 카테고리 선택
  const handleIncomeCategoryClick = (category: Category) => {
    if (selectedIncomeCategory === category) {
      setSelectedIncomeCategory(null);

      return;
    }
    setSelectedIncomeCategory(category);
  };

  // 입금 계좌 클릭 시 해당 입금 계좌 선택
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

  // 수기 입력 제출
  const handleSubmit = async (event: FormEvent) => {
    // const transactionParams = {
    //   categoryId: selectedIncomeCategory?.categoryId,
    //   transactionDate: formatDateToNumber(date),
    //   transactionBalance: numericAmount,
    //   transactionType: "INCOME",
    //   paymentType: selectedTransactionMethod,
    //   transactionTime: formatTime(time),
    //   merchantName: transactionPlace,
    //   memo: memo,
    // };

    // console.log(transactionParams);

    if (
      selectedIncomeCategory?.categoryId == null ||
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
        categoryId: selectedIncomeCategory?.categoryId,
        transactionDate: formatDateToNumber(date),
        transactionBalance: numericAmount,
        transactionType: "INCOME",
        paymentType: selectedTransactionMethod,
        memo: memo,
        transactionTime: formatTime(time),
        merchantName: transactionPlace,
      });

      handleSave();
    } catch (error) {
      // console.log("수기 입력 실패");
    }
  };

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await getCategories(null, 0, "INCOME");

        const categories = response.map(
          (category: { categoryId: number; categoryName: string }) => ({
            categoryId: category.categoryId,
            categoryName: getCategoryName(category.categoryName),
          }),
        );

        setIncomeCategoryList(categories);
      } catch (error) {
        // console.error("카테고리를 불러오는 데 실패했습니다:", error);
      }
    };

    const fetchAccounts = async () => {
      try {
        const accounts = await getMemberAccounts();

        const methodList = [
          { name: "현금" },
          ...accounts.map((account: any) => ({
            name: account.accountName,
            bankCode: account.bankCode,
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
  }, []);

  return (
    <>
      <div className="overflow-y-auto h-[350px]">
        <HandWriteField
          label="입금처"
          value={transactionPlace}
          type="input"
          isSelected={selectedField === "입금처"}
          onClick={() => handleFieldClick("입금처")}
          onChange={(e) => setTransactionPlace(e.target.value)}
        />
        <HandWriteField
          label="카테고리"
          value={selectedIncomeCategory?.categoryName}
          type="text"
          isSelected={selectedField === "수입 카테고리"}
          onClick={() => handleFieldClick("수입 카테고리")}
        />
        <div
          className={`flex flex-col gap-2 transition-all duration-300 ease-in-out overflow-hidden ${
            selectedField === "수입 카테고리" ? "mb-6" : "max-h-0"
          }`}
        >
          {incomeCategoryList.map((category) => {
            const Icon = getCategoryIcon(category.categoryName);

            return (
              <SelectableItem
                key={category.categoryId}
                icon={Icon ? <Icon /> : null}
                name={category.categoryName}
                selectedItem={selectedIncomeCategory?.categoryName}
                onClick={() => handleIncomeCategoryClick(category)}
              />
            );
          })}
        </div>
        <HandWriteField
          label="입금 계좌"
          value={selectedTransactionMethod}
          type="text"
          isSelected={selectedField === "입금 계좌"}
          onClick={() => handleFieldClick("입금 계좌")}
        />
        <div
          className={`flex flex-col gap-2 transition-all duration-300 ease-in-out overflow-hidden ${
            selectedField === "입금 계좌" ? "max-h-[500px] mb-6" : "max-h-0"
          }`}
        >
          {transactionMethodList.map((method, index) => {
            const { Icon } = getBankInfo(method.bankCode ?? "");

            const iconElement =
              method.name === "현금" ? (
                <Finance />
              ) : Icon ? (
                <Icon className="w-12 h-12 rounded-full" />
              ) : null;

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
                <div className="flex justify-center items-center">
                  {/* <CalendarIcon className="w-10 h-10 stroke-gray-400" /> */}
                </div>
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

export default IncomeHandWriteField;
