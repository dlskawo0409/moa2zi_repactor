import { useEffect, useState, useRef } from "react";
import { CircleAlert } from "lucide-react";
import CommonLineChart from "@/components/common/CommonLineChart";
import { getYonoPoints } from "@/services/statistics";
import { YonoPoint } from "@/types/yonoPoint";
import ProfileFillIcon from "@components/svgs/ProfileFillIcon";

interface YonoPointChartProps {
  year: number;
  month: number;
}

const YonoPointChart = ({ year, month }: YonoPointChartProps) => {
  const [chartData, setChartData] = useState<{ day: string; point?: number }[]>([]);
  const [monthScore, setMonthScore] = useState<number>(0);
  const [showTooltip, setShowTooltip] = useState<boolean>(false);

  const chartConfig = {
    point: {
      label: "YONO 지수",
      color: "#FFAA64",
    },
  };

  const tooltipRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (tooltipRef.current && !tooltipRef.current.contains(event.target as Node)) {
        setShowTooltip(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const transactionDate = Number(`${year}${String(month).padStart(2, "0")}00`);

        const { monthScore, dayScoreList }: { monthScore: number; dayScoreList: YonoPoint[] } =
          await getYonoPoints(transactionDate);

        const formattedData = dayScoreList.map(({ score, createdAt }) => {
          const date = new Date(createdAt);
          const month = date.getMonth() + 1;
          const day = date.getDate();

          return {
            day: `${month}/${day}`,
            point: score ?? 0,
          };
        });

        setChartData(formattedData);
        setMonthScore(isNaN(monthScore) ? 0 : monthScore);
      } catch (err) {
        // console.error("YONO 포인트 불러오기 실패:", err);
      }
    };

    fetchData();
  }, [year, month]);

  return (
    <div className="flex flex-col mx-5 gap-3">
      <div className="flex items-center gap-2 relative">
        <div className="text-[20px] font-bold">나의 YONO 지수</div>
        <CircleAlert
          className="size-4 text-neutral-700 cursor-pointer"
          onClick={() => setShowTooltip(!showTooltip)}
        />
        {showTooltip && (
          <div
            ref={tooltipRef}
            className="absolute z-50 flex gap-4 px-3 pt-4 pc:pt-2 right-0 pc:right-5 top-7 w-64 pc:w-96 bg-primary-300 p-2 rounded-md shadow-lg"
          >
            <ProfileFillIcon className="size-24 pc:size-16" />
            <div className="text-sm pc:text-md">
              <div className="font-semibold">요노 쥐수란?!</div>
              <div className="break-keep">
                한달동안 하루하루의 쥐출을 그래프에 "찍"어보며 관리하는 모앗쥐 서비스 자체 점수쥐!
              </div>
            </div>
          </div>
        )}
      </div>

      <CommonLineChart
        chartData={chartData}
        chartConfig={chartConfig}
        averageName="일 평균 YONO 지수"
        score={monthScore}
        month={month}
      />
    </div>
  );
};

export default YonoPointChart;
