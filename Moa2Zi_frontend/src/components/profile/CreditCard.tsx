import ICChipIcon from "@/components/svgs/card/ICChipIcon";
import VisaIcon from "@/components/svgs/card/VisaIcon";
import MasterCardIcon from "@/components/svgs/card/MasterCardIcon";

import HexagonPattern from "@/components/cardPatterns/HexagonPattern";
import WavePattern from "@/components/cardPatterns/WavePattern";
import Wave2Pattern from "@components/cardPatterns/Wave2Pattern";
import WaterDropPattern from "@/components/cardPatterns/WaterDropPattern";
import ZigZagPattern from "@/components/cardPatterns/ZigZagPattern";

interface CreditCardProps {
  name: string;
  code: number;
  number: number;
}

const CreditCard = ({ name, code, number }: CreditCardProps) => {
  const getCardStyle = (code: number) => {
    const styleMap: Record<number, string> = {
      1001: "bg-gradient-to-r from-[#7C7266] to-[#B8B2A1]", // KB국민카드
      1002: "bg-gradient-to-r from-[#1666FF] to-[#6FA8FF]", // 삼성카드
      1003: "bg-gradient-to-r from-[#2C2A26] to-[#756C5F]", // 롯데카드
      1004: "bg-gradient-to-r from-[#0067AC] to-[#63B3ED]", // 우리카드
      1005: "bg-gradient-to-r from-[#0046FF] to-[#5C85FF]", // 신한카드
      1006: "bg-gradient-to-r from-[#000000] to-[#444444]", // 현대카드
      1007: "bg-gradient-to-r from-[#E83E44] to-[#F78C8F]", // BC 바로카드
      1008: "bg-gradient-to-r from-[#00A84D] to-[#4CD98A]", // NH농협카드
      1009: "bg-gradient-to-r from-[#008485] to-[#4FC4C5]", // 하나카드
      1010: "bg-gradient-to-r from-[#0055A2] to-[#4F8DD2]", // IBK기업은행
    };

    return styleMap[code] ?? "bg-gradient-to-r from-slate-400 to-slate-600";
  };

  return (
    <div
      className={`w-full rounded-lg p-5 aspect-[1.8] flex flex-col relative ${getCardStyle(code)} pc:mx-40 pc:my-10`}
    >
      {number % 6 === 0 && <></>}
      {number % 6 === 1 && <WavePattern />}
      {number % 6 === 2 && <WaterDropPattern />}
      {number % 6 === 3 && <ZigZagPattern />}
      {number % 6 === 4 && <Wave2Pattern />}
      {number % 6 === 5 && <HexagonPattern />}

      <div className="flex justify-between">
        <div className="text-white text-[min(5vw,1.5rem)] truncate max-w-[70%] overflow-hidden whitespace-nowrap">
          {name}
        </div>
        <div className="w-[25%]">
          {name.length % 2 === 1 ? (
            <VisaIcon className="w-full h-full" />
          ) : (
            <MasterCardIcon className="w-[90%] h-[90%]" />
          )}
        </div>
      </div>
      <div className="h-[20%]"></div>
      <div className="w-[20%] h-[20%]">
        <ICChipIcon className="w-full h-full" />
      </div>
      <div className="mt-auto font-bold text-white text-[min(5vw,1.5rem)]">
        **** **** **** {number}
      </div>
    </div>
  );
};

export default CreditCard;
