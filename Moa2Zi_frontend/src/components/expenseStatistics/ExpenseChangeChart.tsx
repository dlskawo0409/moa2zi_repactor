import { useEffect, useState } from "react";
import { ChevronDown, ChevronsUp } from "lucide-react";

import CommonLineChart from "@/components/common/CommonLineChart";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuRadioGroup,
  DropdownMenuRadioItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { getTransactionsSum } from "@/services/transaction";

interface ExpenseChangeChartProps {
  year: number;
  month: number;
}

const ExpenseChangeChart = ({ year, month }: ExpenseChangeChartProps) => {
  const [dateMode, setDateMode] = useState<string>("month");
  const [chartData, setChartData] = useState<{ month: string; amount: number }[]>([]);
  const [previousAmount, setPreviousAmount] = useState<number | null>(null);

  const chartConfig = {
    amount: {
      label: "소비 금액",
      color: "#FFAA64",
    },
  };

  useEffect(() => {
    const fetchExpenseData = async () => {
      try {
        if (dateMode === "month") {
          const transactionDate = `${year}${month.toString().padStart(2, "0")}00`;
          const response = await getTransactionsSum({
            transactionDate: Number(transactionDate),
            transactionType: "SPEND",
            unitCount: 6,
            isAscending: false,
          });

          const formattedData = response.map((item: { transactionDate: number; sum: number }) => ({
            month: `${String(item.transactionDate).slice(4, 6)}월`,
            amount: Math.abs(item.sum),
          }));

          // console.log(formattedData);

          setChartData(formattedData);
          if (formattedData.length >= 2) {
            setPreviousAmount(formattedData[5].amount - formattedData[4].amount);
          }
        } else {
          const todayDate = new Date().getDate();
          const lastDate = new Date(year, month, 0).getDate();
          const transactionDate = `${year}${month.toString().padStart(2, "0")}${lastDate.toString()}`;
          const response = await getTransactionsSum({
            transactionDate: Number(transactionDate),
            transactionType: "SPEND",
            unitCount: lastDate,
            isAscending: false,
          });

          const formattedData = response.map((item: { transactionDate: number; sum: number }) => ({
            month: `${String(item.transactionDate).slice(6, 8)}일`,
            amount: Math.abs(item.sum),
          }));

          // console.log(formattedData);

          setChartData(formattedData);
          if (todayDate >= 2) {
            setPreviousAmount(
              formattedData[todayDate - 1].amount - formattedData[todayDate - 2].amount,
            );
          }
        }
      } catch (error) {
        // console.error("소비 데이터 가져오기 실패:", error);
      }
    };

    fetchExpenseData();
  }, [dateMode, month]);

  return (
    <div className="flex flex-col mx-5 gap-3">
      <div className="flex justify-between">
        <div className="flex flex-col gap-1">
          <div className="flex items-center gap-1">
            <div className="text-[20px] font-bold">소비 Flow</div>
          </div>
          <div className="flex gap-1">
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <div className="flex gap-1 cursor-pointer">
                  <div>{dateMode === "day" ? "일별 변화" : "월별 변화"}</div>
                  <div className="flex w-4 h-full justify-center items-center">
                    <ChevronDown />
                  </div>
                </div>
              </DropdownMenuTrigger>
              <DropdownMenuContent>
                <DropdownMenuRadioGroup value={dateMode} onValueChange={setDateMode}>
                  <DropdownMenuRadioItem value="day">
                    <div className="me-2">일별 변화</div>
                  </DropdownMenuRadioItem>
                  <DropdownMenuRadioItem value="month">
                    <div className="me-2">월별 변화</div>
                  </DropdownMenuRadioItem>
                </DropdownMenuRadioGroup>
              </DropdownMenuContent>
            </DropdownMenu>
          </div>
        </div>
        <div className="flex flex-col justify-end">
          <div className="flex justify-end text-[12px] text-neutral-500">
            {dateMode === "day" ? "어제 보다" : "저번달 보다"}
          </div>
          <div
            className={`flex items-center gap-1 font-bold ${previousAmount && previousAmount > 0 ? "text-red-500" : "text-blue-500"}`}
          >
            {/* {previousAmount && previousAmount > 0 ? (
              <ChevronsUp className="w-4 h-4" />
            ) : (
              <ChevronsUp className="w-4 h-4 rotate-180" />
            )} */}
            {previousAmount !== null ? `${previousAmount.toLocaleString()} 원` : "0 원"}
          </div>
        </div>
      </div>

      <div className="mx-6">
        <CommonLineChart
          chartData={chartData}
          chartConfig={chartConfig}
          average={
            chartData.length > 0
              ? Math.round(chartData.reduce((acc, cur) => acc + cur.amount, 0) / chartData.length)
              : 0
          }
          averageName={dateMode === "day" ? "일평균 소비 금액" : "월평균 소비 금액"}
        />
      </div>
    </div>
  );
};

export default ExpenseChangeChart;
