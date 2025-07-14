import { useEffect, useRef, useState } from "react";
import html2canvas from "html2canvas";
import PrizeStampIcon from "@components/svgs/PrizeStampIcon";
import CommonModal from "@components/common/CommonModal";
import CommonButton from "@components/common/CommonButton";
import PrizeModal from "@/components/profile/PrizeModal";
import { getPrize, readPrize } from "@/services/challenge";
import { useUserInfo } from "@/hooks/useUserInfo";
import { Prize } from "@/types/challenge";
import { formatDateToKorean } from "@/utils/formatDate";
import "@/styles/PrizeFlip.css";

const PrizeLocker = () => {
  const { data } = useUserInfo();
  const [prizes, setPrizes] = useState<Prize[]>([]);
  const [flippedIndex, setFlippedIndex] = useState<number | null>(null);
  const [selected, setSelected] = useState<number | null>(null);
  const captureRefs = useRef<(HTMLDivElement | null)[]>([]);

  const fetchData = async () => {
    try {
      if (!data) return;
      const response = await getPrize(data.memberId);
      setPrizes(response.data);
    } catch (error) {
      // console.log(error);
    }
  };

  useEffect(() => {
    fetchData();
  }, [data]);

  const handleAwardRead = async (challengeTimeId: number, index: number) => {
    try {
      await readPrize(challengeTimeId);
      setPrizes((prev) => {
        const updated = [...prev];
        updated[index] = { ...updated[index], type: "EXISTING" };
        return updated;
      });
    } catch (error) {
      // console.log(error);
    }
  };

  const handleCapture = async (index: number) => {
    const target = captureRefs.current[index];
    if (target) {
      const canvas = await html2canvas(target);
      const image = canvas.toDataURL("image/png");

      const link = document.createElement("a");
      link.href = image;
      link.download = `prize-${index + 1}.png`;
      link.click();
    }
  };

  if (prizes.length === 0) {
    return <div className="text-center py-10 text-neutral-600">아직 상장이 없어요!</div>;
  }

  return (
    <div className="p-5 ">
      <div className="grid grid-cols-3 pc:grid-cols-4 gap-4">
        {prizes.map((prize, index) => {
          return (
            <CommonModal
              key={index}
              open={prize.type !== "NEW" && selected === index}
              setOpen={(isOpen) => setSelected(isOpen ? index : null)}
              className="w-64 h-80 pc:w-[20rem] pc:h-[27rem] rounded-sm p-0 bg-white z-40"
              trigger={
                prize.type === "NEW" ? (
                  <div
                    className="w-full aspect-[0.71] [perspective:1000px]"
                    onClick={() => {
                      if (prize.type === "NEW") {
                        setFlippedIndex(index);
                        setTimeout(() => {
                          handleAwardRead(prize.challengeTimeId, index);
                        }, 200);
                      } else {
                        setSelected(index);
                      }
                    }}
                  >
                    <div
                      className={`relative w-full  h-36 pc:h-44 transition-transform duration-300 [transform-style:preserve-3d] ${
                        flippedIndex === index ? "[transform:rotateY(540deg)]" : ""
                      }`}
                    >
                      {/* Front Side */}
                      <div className="absolute w-full h-full backface-hidden border-2 border-[#C59A00] rounded-sm bg-negative-900 flex items-center justify-center">
                        <div className="absolute top-0 left-0 w-[15%] aspect-[1] bg-[#C59A00] rounded-br-full"></div>
                        <div className="absolute top-0 right-0 w-[15%] aspect-[1] bg-[#C59A00] rounded-bl-full"></div>
                        <div className="absolute bottom-0 right-0 w-[15%] aspect-[1] bg-[#C59A00] rounded-tl-full"></div>
                        <div className="absolute bottom-0 left-0 w-[15%] aspect-[1] bg-[#C59A00] rounded-tr-full"></div>
                      </div>

                      {/* Back Side */}
                      <div className="absolute w-full h-full backface-hidden [transform:rotateY(180deg)] border-2 border-[#C59A00] bg-white rounded-sm p-4 flex flex-col items-center justify-center cursor-pointer">
                        <div className="text-center justify-center items-center px-2 pc:px-2 py-4 pc:py-6 w-full aspect-[0.71]">
                          <div className="text-xs font-semibold mt-3 truncate ">{prize.title}</div>
                          <div className="text-xxxs mt-1 font-medium">{prize.awardCategory}</div>
                          <div className="text-xxxs mt-2 text-end">{data?.nickname} 님</div>
                          <div className="text-xxxs mt-5">
                            {formatDateToKorean(prize.challengeEndDate)}
                          </div>
                        </div>
                        <div className="absolute top-0 left-0 w-[15%] aspect-[1] bg-[#C59A00] rounded-br-full"></div>
                        <div className="absolute top-0 right-0 w-[15%] aspect-[1] bg-[#C59A00] rounded-bl-full"></div>
                        <div className="absolute bottom-0 right-0 w-[15%] aspect-[1] bg-[#C59A00] rounded-tl-full"></div>
                        <div className="absolute bottom-0 left-0 w-[15%] aspect-[1] bg-[#C59A00] rounded-tr-full"></div>
                        <PrizeStampIcon className="absolute size-10 pc:size-16 bottom-3 pc:bottom-3 right-3 pc:right-3" />
                      </div>
                    </div>
                  </div>
                ) : (
                  <div
                    className="relative w-full h-36 pc:h-44 border-2 border-[#C59A00] rounded-sm aspect-[0.71] cursor-pointer transition-transform duration-300"
                    onClick={() => setSelected(index)}
                  >
                    <div className="text-center justify-center items-center px-2 pc:px-2 py-4 pc:py-6 w-full aspect-[0.71]">
                      <div className="text-xs font-semibold mt-3">
                        {prize.title ?? "베스트 챌린저상"}
                      </div>
                      <div className="text-xxxs mt-1 font-medium">{prize.awardCategory}</div>
                      <div className="text-xxxs mt-2 text-end">{data?.nickname} 님</div>
                      <div className="text-xxxs mt-5">
                        {formatDateToKorean(prize.challengeEndDate)}
                      </div>
                    </div>
                    <div className="absolute top-0 left-0 w-[15%] aspect-[1] bg-[#C59A00] rounded-br-full"></div>
                    <div className="absolute top-0 right-0 w-[15%] aspect-[1] bg-[#C59A00] rounded-bl-full"></div>
                    <div className="absolute bottom-0 right-0 w-[15%] aspect-[1] bg-[#C59A00] rounded-tl-full"></div>
                    <div className="absolute bottom-0 left-0 w-[15%] aspect-[1] bg-[#C59A00] rounded-tr-full"></div>
                    <PrizeStampIcon className="absolute size-10 pc:size-16 bottom-3 pc:bottom-3 right-3 pc:right-3" />
                  </div>
                )
              }
            >
              <div
                ref={(el) => (captureRefs.current[index] = el)}
                className="relative flex flex-col justify-center items-center w-full bg-white border-4 border-[#C59A00] rounded-sm shadow-lg"
              >
                <PrizeModal prize={prize} />
              </div>

              <CommonButton
                variant="neutral-outline"
                onClick={() => handleCapture(index)}
                className="absolute -bottom-10 text-xxs w-full h-8 rounded transition"
              >
                이미지로 저장하기
              </CommonButton>
            </CommonModal>
          );
        })}
      </div>
    </div>
  );
};

export default PrizeLocker;
