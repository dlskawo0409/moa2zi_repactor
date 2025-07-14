import { motion } from "framer-motion";
import { useEffect, useRef } from "react";
import AwesomeMouseIcon from "@components/svgs/calendar/AwesomeMouseIcon";
import HappyMouseIcon from "@components/svgs/calendar/HappyMouseIcon";
import SurprisedMouseIcon from "@components/svgs/calendar/SurprisedMouseIcon";
import AngryMouseIcon from "@components/svgs/calendar/AngryMouseIcon";
import SadMouseIcon from "@components/svgs/calendar/SadMouseIcon";
import { EMOTION } from "@/types/calendar";

interface EmotionSelectorProps {
  onSelect: (emotion: EMOTION, transactionId: number) => void;
  onClose: (boolean: boolean) => void;
  transactionId: number;
}

const EmotionSelector = ({ onSelect, onClose, transactionId }: EmotionSelectorProps) => {
  const emotionBoxRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      // emotionBoxRef에 연결된 div 내부가 아니면 닫기
      if (
        emotionBoxRef.current && 
        !emotionBoxRef.current.contains(event.target as Node)
      ) {
        onClose(false);
      }
    };
  
    // 'mousedown' 대신 'click'을 쓰는 경우도 많습니다.
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [onClose]);
  

  return (
    <motion.div
      ref={emotionBoxRef}
      initial={{ x: -20, opacity: 0 }}
      animate={{ x: 0, opacity: 1 }}
      exit={{ x: -50, opacity: 0 }}
      transition={{ duration: 0.3 }}
      className="absolute -top-2 -left-1 z-10 flex flex-col gap-0 mt-3"
    >
      <div className="flex justify-center text-xxs">
        소비 감정을 선택해주세요 (최고, 행복, 놀람, 화남, 슬픔)
      </div>
      <div className="flex gap-2 p-1 shadow-md bg-neutral-50 border border-neutral-300 rounded-full cursor-pointer">
        <div
          className="flex justify-center items-center rounded-full w-12 h-12 bg-[#F8B6B6]"
          onClick={() => onSelect("AWESOME", transactionId)}
        >
          <AwesomeMouseIcon />
        </div>
        <div
          className="flex justify-center items-center rounded-full w-12 h-12 bg-[#F8D2B6]"
          onClick={() => onSelect("HAPPY", transactionId)}
        >
          <HappyMouseIcon />
        </div>
        <div
          className="flex justify-center items-center rounded-full w-12 h-12 bg-[#FFF599]"
          onClick={() => onSelect("SURPRISE", transactionId)}
        >
          <SurprisedMouseIcon />
        </div>
        <div
          className="flex justify-center items-center rounded-full w-12 h-12 bg-[#CEEBCA]"
          onClick={() => onSelect("ANGRY", transactionId)}
        >
          <AngryMouseIcon />
        </div>
        <div
          className="flex justify-center items-center rounded-full w-12 h-12 bg-[#C3CCF8]"
          onClick={() => onSelect("SAD", transactionId)}
        >
          <SadMouseIcon />
        </div>
      </div>
    </motion.div>
  );
};

export default EmotionSelector;
