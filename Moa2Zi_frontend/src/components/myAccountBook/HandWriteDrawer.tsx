import { useRef, useState, KeyboardEvent } from "react";
import { Plus, PencilIcon } from "lucide-react";
import CommonButton from "@/components/common/CommonButton";
import CommonDrawer from "@/components/common/CommonDrawer";
import IncomeHandWriteField from "@/components/myAccountBook/IncomeHandWriteField";
import ExpenseHandWriteField from "@/components/myAccountBook/ExpenseHandWriteField";

interface HandWriteDrawerProps {
  addTransactionData: () => void;
}

const HandWriteDrawer = ({ addTransactionData }: HandWriteDrawerProps) => {
  const [profitType, setProfitType] = useState<"income" | "expense">("income");
  const [value, setValue] = useState<string>("0 원");
  const inputRef = useRef<HTMLInputElement>(null);

  const handleKeyDown = (e: KeyboardEvent<HTMLInputElement>) => {
    e.preventDefault();

    if (e.key >= "0" && e.key <= "9") {
      setValue((prev) => {
        const numPart = prev.replace(/,| 원/g, "");
        const newNum = numPart === "0" ? e.key : numPart + e.key;

        if (Number(newNum) > 999_999_999_999) return prev;

        return `${Number(newNum).toLocaleString()} 원`;
      });
    } else if (e.key === "Backspace") {
      setValue((prev) => {
        const numPart = prev.replace(/,| 원/g, "");
        const newNumPart = numPart.slice(0, -1) || "0";
        return `${Number(newNumPart).toLocaleString()} 원`;
      });
    }
  };

  const handleButtonClick = (button: "income" | "expense") => {
    setProfitType(button);
  };

  const handleSave = () => {
    addTransactionData();
  };

  return (
    <div className="flex justify-end">
      <div className="fixed bottom-16 p-5">
        <CommonDrawer
          trigger={
            <div className="flex justify-center items-center bg-primary-500 hover:bg-primary-400 text-white w-14 h-14 rounded-full text-sm font-bold transition-colors ease-in-out cursor-pointer">
              <Plus />
            </div>
          }
        >
          <div className="flex flex-col gap-6 pb-6">
            <div className="flex flex-col mx-6 border-b-2 border-transparent focus-within:border-primary-500">
              <div className="text-2xl">금액</div>
              <div className="relative">
                <div className="absolute text-3xl">{value}</div>
                <div
                  className="flex items-center absolute h-full right-0 cursor-pointer"
                  onClick={() => inputRef.current?.focus()}
                >
                  <PencilIcon />
                </div>
                <input
                  ref={inputRef}
                  className="border-none p-0 w-full shadow-none focus:outline-none placeholder-black text-3xl caret-transparent cursor-pointer [&::-webkit-inner-spin-button]:appearance-none [&::-webkit-outer-spin-button]:appearance-none"
                  type="number"
                  onKeyDown={handleKeyDown}
                />
              </div>
            </div>

            <div className="flex mx-6 gap-12">
              <CommonButton
                variant={profitType === "income" ? "primary" : "neutral"}
                className="w-full h-12"
                onClick={() => handleButtonClick("income")}
              >
                수입
              </CommonButton>
              <CommonButton
                variant={profitType === "expense" ? "primary" : "neutral"}
                className="w-full h-12"
                onClick={() => handleButtonClick("expense")}
              >
                지출
              </CommonButton>
            </div>

            {profitType === "income" ? (
              <IncomeHandWriteField amount={value} handleSave={handleSave} />
            ) : (
              <ExpenseHandWriteField amount={value} handleSave={handleSave} />
            )}
          </div>
        </CommonDrawer>
      </div>
    </div>
  );
};

export default HandWriteDrawer;
