import { useEffect, useState, KeyboardEvent } from "react";
import { ChevronDown } from "lucide-react";

import NavBar from "@/components/common/NavBar";
import ChevronLeftIcon from "@/components/svgs/ChevronLeftIcon";
import ChevronRightIcon from "@/components/svgs/ChevronRightIcon";
import SummaryItems from "@/components/myAccountBook/SummaryItems";
import Transaction from "@/components/myAccountBook/Transaction";
import HandWriteDrawer from "@/components/myAccountBook/HandWriteDrawer";
import TransactionSkeleton from "@/components/myAccountBook/TransactionSkeleton";
import { Input } from "@components/ui/input";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuRadioGroup,
  DropdownMenuRadioItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { useUserInfo } from "@/hooks/useUserInfo";
import { getTransactions } from "@/services/transaction";
import { getKoreanDayOfWeek } from "@/constants/dayOfWeek";
import { TransactionSummary, DayTransaction, TransactionItem } from "@/types/transaction";

const currentDate = new Date();

const MyAccountBookPage = () => {
  const { data, isLoading } = useUserInfo();
  const [transactionData, setTransactionData] = useState<TransactionSummary | null>(null);
  const [year, setYear] = useState<number>(currentDate.getFullYear());
  const [month, setMonth] = useState<number>(currentDate.getMonth() + 1);

  const [search, setSearch] = useState<string>("");
  const [transactionType, setTransactionType] = useState<string>("");

  const handlePrevMonth = () => {
    if (month === 1) {
      setYear(year - 1);
      setMonth(12);
    } else {
      setMonth(month - 1);
    }
  };
  const handleNextMonth = () => {
    if (month === 12) {
      setYear(year + 1);
      setMonth(1);
    } else {
      setMonth(month + 1);
    }
  };

  const fetchData = async () => {
    if (isLoading || !data?.memberId) return;

    setTransactionData(null);

    const formattedMonth = month < 10 ? `0${month}` : `${month}`;
    const formattedDate = `${year}${formattedMonth}00`;

    try {
      await new Promise((resolve) => setTimeout(resolve, 500));

      const response = await getTransactions({
        memberId: data.memberId,
        transactionDate: parseInt(formattedDate),
        transactionType,
        merchantName: search,
      });
      // console.log(response);
      setTransactionData(response);
    } catch (error) {
      // console.error("거래 내역 가져오기 실패", error);
    }
  };

  const handleKeyDown = (event: KeyboardEvent<HTMLInputElement>) => {
    if (event.key === "Enter") {
      fetchData();
    }
  };

  const addTransactionData = () => {
    fetchData();
  };

  useEffect(() => {
    fetchData();
  }, [month, data, transactionType, isLoading]);

  return (
    <div className="pb-16">
      <NavBar />
      <div className="flex flex-col w-full gap-6">
        <div className="flex w-full justify-between items-center px-5">
          <div className="flex justify-center items-center gap-1 w-6 h-6" onClick={handlePrevMonth}>
            <ChevronLeftIcon />
          </div>
          <div className="text-[24px]">
            {year}년 {month}월
          </div>
          <div className="flex justify-center items-center gap-1 w-6 h-6" onClick={handleNextMonth}>
            <ChevronRightIcon />
          </div>
        </div>

        <div className="flex w-full justify-between gap-0.5 px-5 h-12">
          {!transactionData ? (
            <SummaryItems income="-" spend="-" total="-" />
          ) : (
            <SummaryItems
              income={transactionData.incomeSum.toLocaleString()}
              spend={transactionData.spendSum.toLocaleString()}
              total={transactionData.totalSum.toLocaleString()}
            />
          )}
        </div>

        <div className="flex justify-between mx-5 gap-3">
          <Input
            placeholder="검색어를 입력해주세요"
            className="flex-1 border-2 border-neutral-200 bg-neutral-50 text-sm py-2 focus:border-primary-500 focus-visible:ring-primary-500"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            onKeyDown={handleKeyDown}
          />
          <div className="flex w-28 pc:w-32 gap-1 border-2 bg-neutral-50 border-neutral-200 rounded-md items-center px-3 pc:px-4 text-sm">
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <div className="flex text-sm w-full gap-1 cursor-pointer justify-between items-center">
                  <div>
                    {transactionType === "INCOME"
                      ? "수입"
                      : transactionType === "SPEND"
                        ? "지출"
                        : "전체"}
                  </div>
                  <div className="flex w-4 h-full justify-center items-center">
                    <ChevronDown />
                  </div>
                </div>
              </DropdownMenuTrigger>
              <DropdownMenuContent>
                <DropdownMenuRadioGroup value={transactionType} onValueChange={setTransactionType}>
                  <DropdownMenuRadioItem value="">
                    <div className="me-2">전체</div>
                  </DropdownMenuRadioItem>
                  <DropdownMenuRadioItem value="INCOME">
                    <div className="me-2">수입</div>
                  </DropdownMenuRadioItem>
                  <DropdownMenuRadioItem value="SPEND">
                    <div className="me-2">지출</div>
                  </DropdownMenuRadioItem>
                </DropdownMenuRadioGroup>
              </DropdownMenuContent>
            </DropdownMenu>
          </div>
        </div>

        {!transactionData ? (
          <div className="flex flex-col w-full px-5 gap-2">
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
              className={`flex flex-col w-full px-5 gap-2 ${
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

        <HandWriteDrawer addTransactionData={addTransactionData} />
      </div>
    </div>
  );
};

export default MyAccountBookPage;
