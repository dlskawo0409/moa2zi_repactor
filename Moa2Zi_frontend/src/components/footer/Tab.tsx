import { ElementType } from "react";

interface TabProps {
  isActive: boolean;
  fillIconType: ElementType;
  blankIconType: ElementType;
  label: string;
}

const Tab = ({
  isActive: isActive,
  fillIconType: FillIcon,
  blankIconType: BlankIcon,
  label = "홈",
}: TabProps) => {
  return (
    <div className="flex h-full items-center justify-center">
      <div
        className={
          "relative flex flex-col w-12 h-12 pc:w-14 pc:h-14 items-center justify-center gap-0.5 pt-1 rounded-full"
        }
      >
        {isActive ? <FillIcon /> : <BlankIcon />}
        <p className={`text-xxs font-medium`}>{label}</p>
      </div>
    </div>
  );
};

export default Tab;
