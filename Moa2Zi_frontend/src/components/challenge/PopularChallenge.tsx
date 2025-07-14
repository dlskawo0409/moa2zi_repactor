import { ChallengeCardProps } from "@/utils/challengeMapper";
import GoldMedalIcon from "@components/svgs/GoldMedalIcon";
import SilverMedalIcon from "@components/svgs/SilverMedalIcon";
import BronzeMedalIcon from "@components/svgs/BronzeMedalIcon";
import ChallengeCardRight from "@/components/challenge/ChallengeCardRight";

interface ChallengeProps {
  challenge: ChallengeCardProps;
  idx: number;
  openDetailIdx: number | null;
  setOpenDetailIdx: (idx: number | null) => void;
  onJoinChallenge: (challengeTimeId: number) => void;
}

const PopularChallenge = ({
  challenge,
  idx,
  openDetailIdx,
  setOpenDetailIdx,
  onJoinChallenge,
}: ChallengeProps) => {
  const isOpen = openDetailIdx === idx;

  return (
    <div
      className={`relative flex flex-col w-full h-full pc:px-6 rounded-xl shadow-md px-3 py-2 transition duration-300 border-2 cursor-pointer bg-primary-100 border-primary-500`}
      onClick={() => {
        // console.log(
        //   challenge,
        //   "challengeId:",
        //   challenge.challengeId,
        //   "challengeTimeId:",
        //   challenge.challengeTimeId,
        //   "challengeStatus:",
        //   challenge.status,
        // );
        setOpenDetailIdx(isOpen ? null : idx);
      }}
    >
      <div className="flex gap-x-4 pc:gap-x-8 h-full">
        <div className="flex flex-col justify-center w-[35%] pc:w-[30%] h-full gap-4 py-2">
          <div className="flex justify-center items-center w-full text-sm text-primary-500 font-bold">
            <span className="pc:text-3xl text-xl text-primary-800 animate-pulse">
              Top {idx + 1}{" "}
            </span>
          </div>
          <div className="flex h-full justify-center items-center gap-4 mb-4">
            <div className="flex-1 w-full h-full relative">
              <div className="absolute inset-0 flex items-center justify-center">
                <div className="flex items-center justify-center w-full h-full rounded-full">
                  {idx + 1 === 1 && <GoldMedalIcon className="w-full h-full" />}
                  {idx + 1 === 2 && <SilverMedalIcon className="w-full h-full" />}
                  {idx + 1 === 3 && <BronzeMedalIcon className="w-full h-full" />}
                </div>
              </div>
            </div>
          </div>
        </div>

        <ChallengeCardRight challenge={challenge} onJoinChallenge={onJoinChallenge} />
      </div>
    </div>
  );
};

export default PopularChallenge;
