interface SummaryItemProps {
  label: string;
  value: string;
  textColor?: string;
}

const SummaryItem = ({ label, value, textColor }: SummaryItemProps) => (
  <div className="flex-1 text-center">
    <div>{label}</div>
    <div className={textColor}>{value}</div>
  </div>
);

export default SummaryItem;
