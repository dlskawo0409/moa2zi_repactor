import { useRef, ChangeEvent } from "react";
import ChevronRightIcon from "@components/svgs/ChevronRightIcon";

type HandWriteFieldProps = {
  label: string;
  value?: string | null;
  type: "input" | "text";
  isSelected: boolean;
  onClick?: () => void;
  onChange?: (e: ChangeEvent<HTMLInputElement>) => void;
};

const HandWriteField = ({
  label,
  value,
  type,
  isSelected,
  onClick,
  onChange,
}: HandWriteFieldProps) => {
  const inputRef = useRef<HTMLInputElement | null>(null);

  const handleDivClick = () => {
    inputRef.current?.focus();
    if (onClick) {
      onClick();
    }
  };

  return (
    <div
      className="flex px-6 h-[72px] items-center border-t-[1px] border-neutral-200 cursor-pointer"
      onClick={handleDivClick}
    >
      <div className="flex w-28 text-neutral-500">{label}</div>
      <div className="flex w-full items-center justify-between">
        {type === "input" ? (
          <input
            ref={inputRef}
            type="text"
            className="w-full me-2 focus:outline-none placeholder:text-neutral-500 cursor-pointer"
            placeholder={value || "입력해주세요"}
            value={value || ""}
            onChange={onChange}
          />
        ) : (
          <>
            <div className={`${value ? "text-black" : "text-neutral-500"}`}>
              {value || "선택하세요"}
            </div>
            <div>
              <div className={`${isSelected ? "rotate-90" : ""} transition-transform`}>
                <ChevronRightIcon className="text-neutral-400 stroke-[1.5]" />
              </div>
            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default HandWriteField;
