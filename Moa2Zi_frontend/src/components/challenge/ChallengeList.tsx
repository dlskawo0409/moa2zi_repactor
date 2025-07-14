import { useState } from "react";
import { PolarGrid, PolarRadiusAxis, RadialBar, RadialBarChart } from "recharts";
import { getCategoryIcon } from "@/utils/getCategoryIcon";
import { ChallengeCardProps } from "@/utils/challengeMapper";
import { ChartConfig, ChartContainer } from "@/components/ui/chart.tsx";
import ChallengeDetail from "@/components/challenge/ChallengeDetail";
import OtherIcon from "@components/svgs/category/OtherIcon";
import ChallengeCardRight from "@/components/challenge/ChallengeCardRight";

const chartData = [{ browser: "safari", visitors: 200, fill: "ffaa64" }];
const chartConfig = {
  visitors: {
    label: "Visitors",
  },
  safari: {
    label: "Safari",
    color: "hsl(var(--chart-2))",
  },
} satisfies ChartConfig;

const ChallengeList = ({
  challenges,
  onJoinChallenge,
}: {
  challenges: ChallengeCardProps[];
  onJoinChallenge: (challengeTimeId: number) => void;
}) => {
  const [openDetailIdx, setOpenDetailIdx] = useState<number | null>(null);

  return (
    <div className="flex flex-col items-center my-3">
      {challenges.map((challenge, idx) => {
        const isOpen = openDetailIdx === idx;
        const CategoryIcon = getCategoryIcon(challenge.categoryName);

        return (
          <div key={idx} className="w-full mb-4">
            <div
              className={`flex flex-col w-full pc:px-6 rounded-xl shadow-md px-2 py-2 bg-white transform transition duration-300 border-2 cursor-pointer
                ${isOpen ? "border-primary-500 scale-100" : "border-neutral-200 hover:border-primary-500"}`}
              onClick={() => {
                // console.log(
                //   challenge,
                //   "challengeId:",
                //   challenge.challengeId,
                //   "challengeTimeId:",
                //   challenge.challengeTimeId,
                //   "challengeStatus:",
                //   challenge.status,
                // );
                setOpenDetailIdx((prev) => (prev === idx ? null : idx));
              }}
            >
              {/* 카드 상단 */}
              <div className="flex gap-x-4 pc:gap-x-8">
                <div className="flex flex-col justify-center w-[35%] pc:w-[30%] gap-2 py-2">
                  <div className="flex justify-center w-full text-sm text-primary-500 font-bold">
                    {challenge.remainingTime ? (
                      <div className="flex text-center pc:text-md text-xs">
                        ⏱ {challenge.remainingTime}
                        <br />
                        남았습니다.
                      </div>
                    ) : (
                      <div className="flex text-center pc:text-md text-xs">
                        챌린지가 종료됐어요!
                      </div>
                    )}
                  </div>
                  <div className="flex h-20 justify-center items-center gap-4 mb-4">
                    <div className="flex-1 w-24 h-24 relative">
                      <ChartContainer
                        config={chartConfig}
                        className="w-full h-full mx-auto aspect-square"
                      >
                        <RadialBarChart
                          data={chartData}
                          startAngle={90}
                          endAngle={90 + challenge.progress * 3.6}
                          innerRadius={40}
                          outerRadius={55}
                          className="fill-primary-500"
                        >
                          <PolarGrid
                            gridType="circle"
                            radialLines={false}
                            stroke="none"
                            polarRadius={[43, 37]}
                            className="first:fill-muted last:fill-background"
                          />
                          <RadialBar
                            dataKey="visitors"
                            background
                            cornerRadius={10}
                            fill="#ffaa64"
                          />
                          <PolarRadiusAxis tick={false} tickLine={false} axisLine={false} />
                        </RadialBarChart>
                      </ChartContainer>

                      <div className="absolute inset-0 flex items-center justify-center">
                        <div className="flex items-center justify-center w-14 h-14 bg-neutral-100 rounded-full">
                          {CategoryIcon ? (
                            <CategoryIcon className="w-8 h-8 text-primary-500" />
                          ) : (
                            <OtherIcon className="w-8 h-8 text-primary-500" />
                          )}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <ChallengeCardRight challenge={challenge} onJoinChallenge={onJoinChallenge} />
              </div>

              <div
                className={`transition-all duration-300 overflow-hidden ${
                  isOpen ? "max-h-[1000px] mt-4" : "max-h-0"
                }`}
              >
                {isOpen && (
                  <ChallengeDetail
                    challengeId={challenge.challengeId}
                    challengeTimeId={challenge.challengeTimeId}
                    status={challenge.status}
                  />
                )}
              </div>
            </div>
          </div>
        );
      })}
    </div>
  );
};

export default ChallengeList;
