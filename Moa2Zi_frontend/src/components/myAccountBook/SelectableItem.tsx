import { ReactNode } from "react";
import CheckIcon from "@/components/svgs/CheckIcon";

interface SelectableItemProps {
  name: string;
  icon?: ReactNode;
  selectedItem?: string | null;
  nonIcon?: boolean;
  onClick: () => void;
}

const SelectableItem = ({
  name,
  icon,
  selectedItem,
  nonIcon = false,
  onClick,
}: SelectableItemProps) => {
  return (
    <div className="flex justify-between items-center mx-6" onClick={onClick}>
      <div className="flex items-center gap-6">
        {!nonIcon && (
          <div className="flex justify-center items-center w-12 h-12 bg-neutral-200 rounded-full">
            {icon}
          </div>
        )}
        <div>{name}</div>
      </div>
      <CheckIcon className={name === selectedItem ? "text-primary-500" : "text-neutral-300"} />
    </div>
  );
};

export default SelectableItem;
