import { useEffect, useState } from "react";

import CommonButton from "@/components/common/CommonButton";
import ScatterCoins from "@/components/pocketMoney/ScatterCoins";
import { getPocketMonies } from "@/services/pocketMoney";

interface PocketMoneyViewProps {
  isSetting: boolean;
  setIsSetting: (value: boolean) => void;
  thisMonthHave: boolean;
  setThisMonthHave: (value: boolean) => void;
}

const PocketMoneyView = ({
  isSetting,
  setIsSetting,
  thisMonthHave,
  setThisMonthHave,
}: PocketMoneyViewProps) => {
  const [isVisible, setIsVisible] = useState<boolean>(false);
  const [showFirst, setShowFirst] = useState<boolean>(false);
  const [showSecond, setShowSecond] = useState<boolean>(false);
  const [totalAmount, setTotalAmount] = useState<number>(0);
  const [spend, setSpend] = useState<number>(0);
  const [left, setLeft] = useState<number>(0);
  const [dayCanUse, setDayCanUse] = useState<number>(0);
  const [randomNoMoneyMessage, setRandomNoMoneyMessage] = useState<string>("");

  const noMoneyMessages = [
    "하루 0원 도전... 가능할까요?",
    "다음 달은 무지출 도전 어떠세요?",
    "텅장이네요... 힘내요 💪",
  ];

  useEffect(() => {
    const fetchPocketMoney = async () => {
      try {
        const data = await getPocketMonies();
        setThisMonthHave(data.thisMonthHave);
        setTotalAmount(data.totalAmount);
        setSpend(data.spend);
        setLeft(data.left);
        setDayCanUse(data.dayCanUse);
      } catch (error) {
        // console.error("셀프 용돈 불러오기 실패", error);
      }
    };

    fetchPocketMoney();
  }, []);

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

    const randomIndex = Math.floor(Math.random() * noMoneyMessages.length);
    setRandomNoMoneyMessage(noMoneyMessages[randomIndex]);

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
          <div className="text-2xl">이번달 용돈{totalAmount ? "" : "을"}</div>
          <div className="flex border-none p-0 shadow-none focus:outline-none placeholder-black text-2xl">
            {totalAmount ? `${totalAmount.toLocaleString()}원` : "설정해주세요!"}
          </div>
        </div>

        <div className="flex flex-col justify-center items-center">
          <div
            className={`flex flex-col items-center gap-10 ${
              isVisible ? "transition-opacity duration-1000 opacity-100" : "opacity-0"
            }`}
          >
            <div>
              <div>지금까지</div>
              <div>{spend ? `${spend.toLocaleString()} 원 썼어요!` : "지출이 없어요!"}</div>
            </div>
            <ScatterCoins />
            <div>
              <div
                className={`transition-all duration-700 ease-out ${
                  showFirst ? "translate-x-0 opacity-100" : "-translate-x-full opacity-0"
                }`}
              >
                {left <= 0
                  ? "이번달 용돈을 다 썼어요!"
                  : `남은 용돈은 ${left ? `${left.toLocaleString()}` : "-"} 원이에요!`}
              </div>
              <div
                className={`transition-all duration-700 ease-out ${
                  showSecond ? "translate-x-0 opacity-100" : "-translate-x-full opacity-0"
                }`}
              >
                {left <= 0
                  ? randomNoMoneyMessage
                  : `하루 ${dayCanUse.toLocaleString()}원 써야 살 수 있어요~`}
              </div>
            </div>
          </div>
        </div>
        <div className="flex w-full justify-between items-center px-5 mt-auto">
          <CommonButton
            variant="primary"
            className="w-full"
            onClick={() => setIsSetting(!isSetting)}
          >
            {thisMonthHave ? "다음달 용돈 설정하기" : "이번달 용돈 설정하기"}
          </CommonButton>
        </div>
      </div>
    </>
  );
};

export default PocketMoneyView;
