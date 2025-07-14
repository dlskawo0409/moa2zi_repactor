import { ComponentType } from "react";

interface AssetItemProps {
  Icon?: ComponentType<{ className?: string }>;
  name: string;
  title: string;
  number: string;
}

const AssetItem = ({ Icon, name, title, number }: AssetItemProps) => {
  return (
    <div className="mb-2 p-3 border rounded-lg flex items-center gap-3">
      {Icon && <Icon className="w-14 h-14 shrink-0" />}
      <div>
        <div className="text-sm text-neutral-600">{name}</div>
        <div className="font-medium">{title}</div>
        <div className="text-sm text-neutral-500">{number}</div>
      </div>
    </div>
  );
};

export default AssetItem;
