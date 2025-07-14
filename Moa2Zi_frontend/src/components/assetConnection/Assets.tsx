import { useState, useMemo } from "react";
import { bankList } from "@/constants/bankList";
import { cardIssuerList } from "@/constants/cardIssuerList";

const generateRandomBooleans = (length: number) => {
  return Array.from({ length }, () => Math.random() < 0.1);
};

const Assets = () => {
  const [spinningBankIndex, setSpinningBankIndex] = useState<number | null>(null);
  const [spinningCardIndex, setSpinningCardIndex] = useState<number | null>(null);
  const [spinningBank2Index, setSpinningBank2Index] = useState<number | null>(null);

  const duplicatedBanks = Array(10).fill(bankList).flat();
  const duplicatedCards = Array(10).fill(cardIssuerList).flat();
  const fullBanks = Array(10).fill(bankList).flat();
  const duplicatedBanks2 = [...fullBanks.slice(6), ...fullBanks.slice(0, 6)];

  const bankVisible = useMemo(
    () => generateRandomBooleans(duplicatedBanks.length),
    [duplicatedBanks.length],
  );
  const cardVisible = useMemo(
    () => generateRandomBooleans(duplicatedCards.length),
    [duplicatedCards.length],
  );
  const bank2Visible = useMemo(
    () => generateRandomBooleans(duplicatedBanks2.length),
    [duplicatedBanks2.length],
  );

  return (
    <div className="space-y-16">
      {/* 은행 슬라이드 */}
      <div className="relative w-full">
        <div className="flex w-max animate-slide-left gap-8">
          {duplicatedBanks.map((bank, idx) => (
            <div key={idx} className="relative flex flex-col items-center">
              <div
                onClick={() => setSpinningBankIndex(idx)}
                className={`w-20 h-20 rounded-full cursor-pointer transition-transform duration-300 ${
                  spinningBankIndex === idx ? "animate-coin-spin" : ""
                }`}
                style={{
                  transformStyle: "preserve-3d",
                  backfaceVisibility: "hidden",
                }}
              >
                <bank.Icon className="w-full h-full rounded-full" />
              </div>

              {bankVisible[idx] && (
                <img
                  src="/logo.png"
                  alt="effect"
                  className={`absolute w-8 h-8 bottom-20 left-6 transition-opacity duration-1000 ${
                    spinningBankIndex === idx
                      ? "animate-fly-away pointer-events-none"
                      : "opacity-100"
                  }`}
                />
              )}
            </div>
          ))}
        </div>
      </div>

      {/* 카드 슬라이드 */}
      <div className="relative w-full">
        <div className="flex w-max animate-slide-right gap-8 justify-end">
          {duplicatedCards.map((card, idx) => (
            <div key={idx} className="relative flex flex-col items-center">
              <div
                onClick={() => setSpinningCardIndex(idx)}
                className={`w-20 h-20 rounded-full cursor-pointer transition-transform duration-300 ${
                  spinningCardIndex === idx ? "animate-coin-spin" : ""
                }`}
                style={{
                  transformStyle: "preserve-3d",
                  backfaceVisibility: "hidden",
                }}
              >
                <card.Icon className="w-full h-full rounded-full" />
              </div>

              {cardVisible[idx] && (
                <img
                  src="/logo.png"
                  alt="effect"
                  className={`absolute w-8 h-8 bottom-20 left-6 transition-opacity duration-1000 ${
                    spinningCardIndex === idx
                      ? "animate-fly-away pointer-events-none"
                      : "opacity-100"
                  } ${idx < duplicatedCards.length / 2 ? "scale-x-[-1]" : ""}`}
                />
              )}
            </div>
          ))}
        </div>
      </div>

      {/* 은행 슬라이드2 */}
      <div className="relative w-full">
        <div className="flex w-max animate-slide-left-2 gap-8">
          {duplicatedBanks2.map((bank, idx) => (
            <div key={idx} className="relative flex flex-col items-center">
              <div
                onClick={() => setSpinningBank2Index(idx)}
                className={`w-20 h-20 rounded-full cursor-pointer transition-transform duration-300 ${
                  spinningBank2Index === idx ? "animate-coin-spin" : ""
                }`}
                style={{
                  transformStyle: "preserve-3d",
                  backfaceVisibility: "hidden",
                }}
              >
                <bank.Icon className="w-full h-full rounded-full" />
              </div>

              {bank2Visible[idx] && (
                <img
                  src="/logo.png"
                  alt="effect"
                  className={`absolute w-8 h-8 bottom-20 left-6 transition-opacity duration-1000 ${
                    spinningBank2Index === idx
                      ? "animate-fly-away pointer-events-none"
                      : "opacity-100"
                  }`}
                />
              )}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default Assets;
