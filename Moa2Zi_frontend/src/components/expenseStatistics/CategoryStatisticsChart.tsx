import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { DotIcon } from "lucide-react";

import ChevronRightIcon from "@/components/svgs/ChevronRightIcon";
import CommonPieChart from "@/components/common/CommonPieChart";
import { getCategoryStatistics } from "@/services/statistics";
import { pieChartConfig, defaultColor } from "@/constants/pieChartConfig";

interface CategoryStatisticsChartProps {
  year: number;
  month: number;
}

const CategoryStatisticsChart = ({ year, month }: CategoryStatisticsChartProps) => {
  const navigate = useNavigate();

  interface CategoryData {
    categoryId: number;
    categoryName: string;
    percent: number;
    sum: number;
  }

  const [pieChartData, setPieChartData] = useState<
    { id: number; category: string; percentage: number; amount: number }[]
  >([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  useEffect(() => {
    const fetchCategoryStatistics = async () => {
      try {
        const transactionDate = `${year}${month.toString().padStart(2, "0")}00`;
        const response: CategoryData[] = await getCategoryStatistics({
          transactionDate: Number(transactionDate),
          unitCount: 1,
          unitRankCount: 4,
        });

        const formattedData = response.map((item) => ({
          id: item.categoryId,
          category: item.categoryName,
          percentage: item.percent,
          amount: item.sum,
        }));

        setPieChartData(formattedData);
      } catch (error) {
        // console.error("카테고리 통계 데이터를 불러오는 중 오류 발생", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchCategoryStatistics();
  }, [month]);

  if (isLoading) return null;

  return (
    <div className="flex flex-col mx-5 mb-5 gap-3">
      <div className="flex justify-between">
        <div className="flex">
          <div className="text-[20px] font-bold">카테고리별 분석</div>
        </div>
        <div className="flex items-end">
          <div
            className="flex justify-end text-[12px] text-neutral-500 cursor-pointer"
            onClick={() => navigate(`category/${year}${month.toString().padStart(2, "0")}00`)}
          >
            전체보기
          </div>
        </div>
      </div>
      <div className="mx-6">
        <CommonPieChart chartData={pieChartData} chartConfig={pieChartConfig} />
      </div>

      <div className="flex flex-col gap-1">
        {pieChartData.map((data) => {
          const categoryConfig = pieChartConfig[data.category] || {
            label: data.category,
            color: defaultColor,
          };

          return (
            <div key={data.category} className="flex justify-between">
              <div className="flex items-center">
                <DotIcon color={categoryConfig.color} size={30} />
                <div>
                  {categoryConfig.label} {data.percentage}%
                </div>
              </div>
              <div
                className="flex items-center gap-2 cursor-pointer"
                onClick={() =>
                  navigate(`category/${data.id}/${year}${month.toString().padStart(2, "0")}00`)
                }
              >
                <div>{data.amount.toLocaleString()}원</div>
                <ChevronRightIcon />
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default CategoryStatisticsChart;
