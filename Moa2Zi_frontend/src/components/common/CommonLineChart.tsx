import { useEffect, useState } from "react";

import { LineChart, Line, XAxis, CartesianGrid, ReferenceArea } from "recharts";
import { ChartContainer, ChartTooltip, ChartTooltipContent } from "@/components/ui/chart";

interface CommonLineChartProps {
  chartData: any[];
  chartConfig: any;
  color?: string;
  average?: number;
  averageName?: string;
  score?: number;
  month?: number;
}

export default function CommonLineChart({
  chartData,
  chartConfig,
  color = "#FFAA64",
  average,
  averageName,
  score = 0,
  month,
}: CommonLineChartProps) {
  if (chartData.length === 0) return null;

  const [XAxixKey, YAxixKey] = Object.keys(chartData[0]);

  const [animatedY, setAnimatedY] = useState<number>(0);

  useEffect(() => {
    setAnimatedY(0);

    const interval = setInterval(() => {
      if (score >= 0) setAnimatedY((prevY) => Math.min(prevY + score * 0.002, score));
      else setAnimatedY((prevY) => Math.max(prevY + score * 0.002, score));
    }, 1);

    setTimeout(() => clearInterval(interval), 5000);
  }, [month]);

  return (
    <div>
      {score ? (
        <div className="flex font-bold text-[primary-500]">{`${month}월 YONO 점수: ${animatedY.toFixed(2)}`}</div>
      ) : (
        <></>
      )}
      <ChartContainer config={chartConfig}>
        <LineChart
          accessibilityLayer
          data={chartData}
          margin={{
            top: 12,
            left: 12,
            right: 12,
          }}
        >
          <ReferenceArea y1={0} y2={animatedY} fill={"#ff5a5f"} fillOpacity={0.1} />

          <CartesianGrid vertical={false} />
          <XAxis
            dataKey={XAxixKey}
            tickLine={false}
            axisLine={false}
            tickMargin={8}
            width={0}
            tickFormatter={(value) => value.slice(0, 5)}
          />

          <ChartTooltip cursor={false} content={<ChartTooltipContent hideLabel />} />

          <Line
            dataKey={YAxixKey}
            type="linear"
            stroke={color}
            strokeWidth={2}
            dot={{ fill: "white" }}
            activeDot={{ r: 6 }}
          />

          <Line
            dataKey={() => average}
            stroke="#A9A9A9"
            dot={false}
            activeDot={false}
            name={averageName}
            strokeDasharray="5 5"
          />
        </LineChart>
      </ChartContainer>
    </div>
  );
}
