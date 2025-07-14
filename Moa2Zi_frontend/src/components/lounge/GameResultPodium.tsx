import { QuizRank } from "@/types/lounge";

interface GameResultPodiumProps {
  rankList: QuizRank[];
}

const GameResultPodium = ({ rankList }: GameResultPodiumProps) => {
  return (
    <div className="flex gap-1 w-2/3 items-end text-sm font-semibold">
      <div className="w-1/3">
        <div className="text-center">{rankList[1]?.nickname}</div>
        <div className="bg-primary-400 w-full h-28"></div>
      </div>
      <div className="w-1/3">
        <div className="text-center">{rankList[0]?.nickname}</div>
        <div className="bg-primary-600 w-full h-36"></div>
      </div>
      <div className="w-1/3">
        <div className="text-center">{rankList[2]?.nickname}</div>
        <div className="bg-primary-200 w-full h-20"></div>
      </div>
    </div>
  );
};

export default GameResultPodium;
