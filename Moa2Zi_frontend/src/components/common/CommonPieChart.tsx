import { Pie, PieChart, Sector } from "recharts";
import { PieSectorDataItem } from "recharts/types/polar/Pie";

import {
  ChartConfig,
  ChartContainer,
  ChartTooltip,
  ChartTooltipContent,
} from "@/components/ui/chart";

interface CommonPieChartProps {
  chartData: any[];
  chartConfig: ChartConfig;
}

export function CommonPieChart({ chartData, chartConfig }: CommonPieChartProps) {
  const processedChartData = chartData.map((item) => ({
    ...item,
    fill: chartConfig[item.category]?.color || "#D9D9D9",
  }));

  return (
    <ChartContainer config={chartConfig} className="mx-auto aspect-square max-h-[250px]">
      <PieChart>
        <ChartTooltip cursor={false} content={<ChartTooltipContent hideLabel />} />
        <Pie
          data={processedChartData}
          dataKey="percentage"
          nameKey="category"
          innerRadius={60}
          strokeWidth={5}
          activeIndex={0}
          activeShape={({ outerRadius = 0, ...props }: PieSectorDataItem) => (
            <Sector {...props} outerRadius={outerRadius + 10} />
          )}
        />
      </PieChart>
    </ChartContainer>
  );
}

export default CommonPieChart;
