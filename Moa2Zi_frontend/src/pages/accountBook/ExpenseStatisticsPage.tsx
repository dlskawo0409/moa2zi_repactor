import { useState } from "react";

import NavBar from "@/components/common/NavBar";
import ChevronLeftIcon from "@/components/svgs/ChevronLeftIcon";
import ChevronRightIcon from "@/components/svgs/ChevronRightIcon";
import ExpenseChangeChart from "@/components/expenseStatistics/ExpenseChangeChart";
import CategoryStatisticsChart from "@/components/expenseStatistics/CategoryStatisticsChart";
import YonoPointChart from "@/components/expenseStatistics/YonoPointChart";

const currentDate = new Date();

const ExpenseStatisticsPage = () => {
  const [year, setYear] = useState<number>(currentDate.getFullYear());
  const [month, setMonth] = useState<number>(currentDate.getMonth() + 1);

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

  return (
    <div>
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
        {/* 나의 YONO 지수 */}
        <YonoPointChart year={year} month={month} />

        {/* 월별&일별 소비 변화량 */}
        <ExpenseChangeChart year={year} month={month} />

        {/* 카테고리별 분석 */}
        <CategoryStatisticsChart year={year} month={month} />
      </div>
    </div>
  );
};

export default ExpenseStatisticsPage;
