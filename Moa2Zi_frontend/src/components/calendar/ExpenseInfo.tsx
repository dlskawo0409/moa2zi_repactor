import { ChevronsUp, ChevronsDown } from "lucide-react";

interface ExpenseInfoProps {
  selectedYear: number;
  selectedMonth: number;
  spendSum: number;
  amountDiffPrevMonth: number;
}

const ExpenseInfo = ({
  selectedYear,
  selectedMonth,
  spendSum,
  amountDiffPrevMonth,
}: ExpenseInfoProps) => {
  return (
    <div className="flex px-5 pc:px-10 justify-between my-5">
      <div className="pc:text-lg">
        <div>
          {selectedYear}년 {selectedMonth}월의 소비량은
        </div>
        <div className="flex gap-1">
          <div className="font-bold">{spendSum.toLocaleString()}원</div>
          <div>입니다</div>
        </div>
      </div>
      <div className="flex flex-col justify-end gap-1">
        <div className="flex justify-end text-sm text-neutral-500">전월대비</div>
        <div
          className={`flex items-center gap-1 pc:text-lg ${amountDiffPrevMonth > 0 && "text-negative-500"} ${amountDiffPrevMonth === 0 && "text-black"} ${amountDiffPrevMonth < 0 && "text-positive-500"} font-bold`}
        >
          {/* {amountDiffPrevMonth > 0 && <ChevronsUp className="w-4 h-4" />}
          {amountDiffPrevMonth < 0 && <ChevronsDown className="w-4 h-4" />} */}
          {amountDiffPrevMonth.toLocaleString()}원
        </div>
      </div>
    </div>
  );
};

export default ExpenseInfo;
