import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { ArrowLeft, DotIcon } from "lucide-react";

import ChevronRightIcon from "@/components/svgs/ChevronRightIcon";
import CommonPieChart from "@/components/common/CommonPieChart";
import { getCategoryStatistics } from "@/services/statistics";
import { pieChartConfig, defaultColor } from "@/constants/pieChartConfig";

const CategoryStatisticsPage = () => {
  interface CategoryData {
    categoryId: number;
    categoryName: string;
    percent: number;
    sum: number;
  }

  const [pieChartData, setPieChartData] = useState<
    { id: number; category: string; percentage: number; amount: number }[]
  >([]);

  const navigate = useNavigate();
  const { transactionDate } = useParams<{ transactionDate: string }>();

  useEffect(() => {
    const fetchCategoryStatistics = async () => {
      try {
        const response: CategoryData[] = await getCategoryStatistics({
          transactionDate: Number(transactionDate),
          unitCount: 1,
          unitRankCount: 20,
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
      }
    };

    fetchCategoryStatistics();
  }, [transactionDate]);

  return (
    <div className="flex flex-col mb-5 gap-3">
      <div className="sticky top-0 flex items-center w-full h-[55px] px-5 bg-white border-b-[1px]">
        <div className="cursor-pointer" onClick={() => navigate(-1)}>
          <ArrowLeft />
        </div>
        <div className="absolute left-1/2 -translate-x-1/2 text-xl font-bold">
          카테고리별 보고서
        </div>
      </div>

      <div className="mx-5">
        <CommonPieChart chartData={pieChartData} chartConfig={pieChartConfig} />
      </div>

      <div className="flex flex-col mx-5 gap-1">
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
                  navigate(`/account-book/statistics/category/${data.id}/${transactionDate}`)
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

export default CategoryStatisticsPage;
