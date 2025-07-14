import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { ArrowLeft, ChevronDown } from "lucide-react";

import Transaction from "@/components/myAccountBook/Transaction";
import TransactionSkeleton from "@/components/myAccountBook/TransactionSkeleton";
import { useUserInfo } from "@/hooks/useUserInfo";
import { getTransactions } from "@/services/transaction";
import { getSubCategories } from "@/services/category";
import { getKoreanDayOfWeek } from "@/constants/dayOfWeek";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuRadioGroup,
  DropdownMenuRadioItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { TransactionSummary, DayTransaction, TransactionItem } from "@/types/transaction";
import { Category } from "@/types/category";

const CategoryExpensePage = () => {
  const navigate = useNavigate();
  const { categoryId } = useParams<{ categoryId: string }>();
  const { transactionDate } = useParams<{ transactionDate: string }>();

  const { data, isLoading } = useUserInfo();
  const [transactionData, setTransactionData] = useState<TransactionSummary | null>(null);
  const [Category, setCategory] = useState<Category>();
  const [selectedSubCategoryId, setSelectedSubCategoryId] = useState<string>("");
  const year = transactionDate?.substring(0, 4);
  const month = transactionDate?.substring(4, 6);

  const fetchData = async () => {
    if (isLoading || !data?.memberId || !transactionDate) return;

    setTransactionData(null);

    try {
      await new Promise((resolve) => setTimeout(resolve, 500));

      const searchId = selectedSubCategoryId
        ? selectedSubCategoryId || categoryId
        : (categoryId ?? "0");

      const response = await getTransactions({
        memberId: data.memberId,
        transactionDate: parseInt(transactionDate),
        transactionType: "SPEND",
        categoryId: parseInt(searchId ?? "0"),
      });
      // console.log(response);
      setTransactionData(response);
    } catch (error) {
      // console.error("거래 내역 가져오기 실패", error);
    }
  };

  const fetchSubCategories = async () => {
    if (isLoading || !data?.memberId || !categoryId) return;

    try {
      const response = await getSubCategories(parseInt(categoryId), 0, "SPEND");

      setCategory(response[0]);
    } catch (error) {
      // console.error("서브 카테고리 가져오기 실패", error);
    }
  };

  useEffect(() => {
    if (!isLoading && data?.memberId && categoryId) {
      fetchSubCategories();
    }
  }, [isLoading, data]);

  useEffect(() => {
    fetchData();
  }, [month, data, isLoading, categoryId, selectedSubCategoryId]);

  return (
    <div>
      <div className="sticky top-0 flex items-center w-full h-[55px] px-5 bg-white border-b-[1px]">
        <div className="cursor-pointer" onClick={() => navigate(-1)}>
          <ArrowLeft />
        </div>
        <div className="absolute left-1/2 -translate-x-1/2 text-xl font-bold">
          카테고리별 보고서
        </div>
      </div>

      <div className="flex w-full justify-center items-center px-5 mt-5">
        <div className="text-[24px]">
          {year}년 {month}월
        </div>
      </div>

      <div className="flex flex-col mx-5">
        <div className="text-xl">{Category?.categoryName}</div>
        <div className="text-2xl">
          {!transactionData ? "-" : transactionData.spendSum.toLocaleString()}원
        </div>
      </div>

      <div className="flex justify-end mx-5 gap-3">
        <div className="flex gap-1 rounded-md items-center px-2 pc:px-3 text-sm">
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <div className="flex gap-1 cursor-pointer text-lg">
                {Category?.categoryList?.find(
                  (sub) => sub.subCategoryId?.toString() === selectedSubCategoryId,
                )?.subCategoryName ?? "전체"}
                <div className="flex w-4 h-full justify-center items-center">
                  <ChevronDown />
                </div>
              </div>
            </DropdownMenuTrigger>
            <DropdownMenuContent className="mx-5">
              <DropdownMenuRadioGroup
                value={selectedSubCategoryId}
                onValueChange={(value) => setSelectedSubCategoryId(value)}
              >
                <DropdownMenuRadioItem value="">전체</DropdownMenuRadioItem>
                {Category?.categoryList?.map((sub) => (
                  <DropdownMenuRadioItem
                    key={sub.subCategoryId}
                    value={sub.subCategoryId?.toString() || ""}
                  >
                    {sub.subCategoryName}
                  </DropdownMenuRadioItem>
                ))}
              </DropdownMenuRadioGroup>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
      </div>

      {!transactionData ? (
        <div className="flex flex-col w-full px-5 gap-2 my-5">
          <div className="flex w-full border-b-[1px] border-neutral-900">일 요일</div>
          <TransactionSkeleton />
          <TransactionSkeleton />
          <TransactionSkeleton />
          <TransactionSkeleton />
          <TransactionSkeleton />
          <TransactionSkeleton />
          <TransactionSkeleton />
        </div>
      ) : transactionData.incomeSum === 0 &&
        transactionData.spendSum === 0 &&
        transactionData.totalSum === 0 ? (
        <div className="flex justify-center items-center text-neutral-500 text-sm h-40">
          거래내역이 없어요
        </div>
      ) : (
        transactionData.transactionWithDate.map((dayTransaction: DayTransaction) => (
          <div
            key={dayTransaction.dayId}
            className={`flex flex-col w-full px-5 gap-2 my-5 ${
              dayTransaction.transactionList.length === 0 ? "hidden" : ""
            }`}
          >
            <div className="flex w-full border-b-[1px] border-neutral-900">
              {parseInt(String(dayTransaction.transactionDate).slice(-2), 10)}일{" "}
              {getKoreanDayOfWeek(dayTransaction.dayOfWeek)}
            </div>
            {dayTransaction.transactionList.map((transaction: TransactionItem) => (
              <Transaction key={transaction.transactionId} transaction={transaction} />
            ))}
          </div>
        ))
      )}
    </div>
  );
};

export default CategoryExpensePage;
