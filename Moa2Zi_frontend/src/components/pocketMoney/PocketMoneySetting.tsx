import { useEffect, useRef, useState, KeyboardEvent } from "react";
import { Toaster, toast } from "sonner";
import { PencilIcon } from "lucide-react";

import CommonButton from "@/components/common/CommonButton";
import CollectCoins from "@/components/pocketMoney/CollectCoins";
import { postPocketMonies } from "@/services/pocketMoney";
import { getTransactionsSum } from "@/services/transaction";

interface PocketMoneySettingProps {
  isSetting: boolean;
  setIsSetting: (value: boolean) => void;
  thisMonthHave: boolean;
}

const PocketMoneySetting = ({
  isSetting,
  setIsSetting,
  thisMonthHave,
}: PocketMoneySettingProps) => {
  const [isVisible, setIsVisible] = useState<boolean>(false);
  const [showFirst, setShowFirst] = useState<boolean>(false);
  const [showSecond, setShowSecond] = useState<boolean>(false);
  const [notInputError, setNotInputError] = useState<boolean>(false);

  const [lastMonthUsed, setLastMonthUsed] = useState<number>(0);
  const [value, setValue] = useState<string>("0 원");

  const inputRef = useRef<HTMLInputElement>(null);

  const fetchExpenseData = async () => {
    try {
      const todayDate = new Date();
      const year = todayDate.getFullYear();
      const month = String(todayDate.getMonth()).padStart(2, "0");

      const response = await getTransactionsSum({
        transactionDate: Number(`${year}${month}00`),
        transactionType: "SPEND",
        unitCount: 1,
        isAscending: false,
      });

      setLastMonthUsed(Math.abs(response[0].sum));
    } catch (error) {
      // console.error("소비 데이터 가져오기 실패:", error);
    }
  };

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

  const handleGivePocketMoney = async () => {
    const totalAmount = Number(value.replace(/,| 원/g, ""));

    if (totalAmount <= 0) {
      toast("금액을 입력해주세요.", {
        duration: 3000,
      });

      setNotInputError(true);
      setTimeout(() => setNotInputError(false), 2000);

      return;
    }

    const delay = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));

    try {
      await postPocketMonies({
        totalAmount,
        thisMonthHave,
      });

      // alert("용돈설정됨");
    } catch (error) {
      toast("다음달 용돈이 이미 설정되어있어요", {
        duration: 3000,
      });

      // console.error("용돈 설정 실패:", error);

      await delay(3000);
    } finally {
      setIsSetting(!isSetting);
    }
  };

  useEffect(() => {
    const timeout = setTimeout(() => {
      setIsVisible(true);
    }, 500);

    const firstTimeout = setTimeout(() => {
      setShowFirst(true);
    }, 1000);

    const secondTimeout = setTimeout(() => {
      setShowSecond(true);
    }, 1500);

    fetchExpenseData();

    return () => {
      clearTimeout(timeout);
      clearTimeout(firstTimeout);
      clearTimeout(secondTimeout);
    };
  }, []);

  return (
    <>
      <div className="flex flex-col gap-5">
        <div className="flex flex-col mx-5 mt-5">
          <div className="flex flex-col border-b-2 border-transparent focus-within:border-primary-500">
            <div className="text-2xl">{thisMonthHave ? "다음달" : "이번달"} 용돈 설정하기</div>
            <div className={`relative ${notInputError ? "border-negative-500 animate-shake" : ""}`}>
              <div className="absolute text-2xl">{value}</div>
              <div
                className="flex items-center absolute h-full right-0 cursor-pointer"
                onClick={() => inputRef.current?.focus()}
              >
                <PencilIcon />
              </div>
              <input
                ref={inputRef}
                className="border-none p-0 w-full shadow-none focus:outline-none placeholder-black text-2xl caret-transparent cursor-pointer [&::-webkit-inner-spin-button]:appearance-none [&::-webkit-outer-spin-button]:appearance-none "
                type="number"
                onKeyDown={handleKeyDown}
              />
            </div>
          </div>
        </div>

        <div className="flex flex-col justify-center items-center flex-grow">
          <div
            className={`flex flex-col items-center gap-10 ${
              isVisible ? "transition-opacity duration-1000 opacity-100" : "opacity-0"
            }`}
          >
            <div>
              <div>고정 지출을 제외한</div>
              <div>나의 용돈을 얼마를 줄까요?</div>
            </div>
            <CollectCoins />
            <div>
              <div
                className={`transition-all duration-700 ease-out ${
                  showFirst ? "translate-x-0 opacity-100" : "-translate-x-full opacity-0"
                }`}
              >
                지난달엔
              </div>
              <div
                className={`transition-all duration-700 ease-out ${
                  showSecond ? "translate-x-0 opacity-100" : "-translate-x-full opacity-0"
                }`}
              >
                {lastMonthUsed.toLocaleString()} 원 썼어요!
              </div>
            </div>
          </div>
        </div>
      </div>

      <Toaster position="top-center" toastOptions={{ className: "custom-toast-negative" }} />

      <div className="flex w-full justify-between items-center px-5">
        <CommonButton variant="primary" className="w-full" onClick={handleGivePocketMoney}>
          용돈 주기
        </CommonButton>
      </div>
    </>
  );
};

export default PocketMoneySetting;
