import VerticalLine from "@/components/common/VerticalLine";
import SummaryItem from "@/components/myAccountBook/SummaryItem";

interface SummaryItemsProps {
  income: string;
  spend: string;
  total: string;
}

const SummaryItems = ({ income, spend, total }: SummaryItemsProps) => {
  return (
    <>
      <SummaryItem label="수입" value={income} textColor="text-positive-500" />
      <VerticalLine />
      <SummaryItem label="지출" value={spend} textColor="text-negative-500" />
      <VerticalLine />
      <SummaryItem label="합계" value={total} />
    </>
  );
};

export default SummaryItems;
