import { useNavigate } from "react-router-dom";

import GoldMedalIcon from "@/components/svgs/GoldMedalIcon";
import SilverMedalIcon from "@/components/svgs/SilverMedalIcon";
import BronzeMedalIcon from "@/components/svgs/BronzeMedalIcon";
import GameIcon from "@/components/svgs/GameIcon";

interface GameHistoryItemProps {
  loungeId: number;
  gameId: number;
  rank: number;
  time: string;
  name: string;
  participant: number;
}

const GameHistoryItem = ({
  rank,
  name,
  time,
  participant,
  loungeId,
  gameId,
}: GameHistoryItemProps) => {
  const navigate = useNavigate();

  const getBorderColor = () => {
    switch (rank) {
      case 1:
        return "#FFE27A";
      case 2:
        return "#E0E0E0";
      case 3:
        return "#FF9838";
      default:
        return "#000000";
    }
  };

  const renderMedalIcon = () => {
    switch (rank) {
      case 1:
        return <GoldMedalIcon className="w-16 h-16" />;
      case 2:
        return <SilverMedalIcon className="w-16 h-16" />;
      case 3:
        return <BronzeMedalIcon className="w-16 h-16" />;
      default:
        return (
          <div className="flex justify-center items-center w-16 h-16 text-2xl font-bold">
            {rank}
          </div>
        );
    }
  };

  return (
    <div
      className="flex items-center gap-4 w-full h-20 border-l-4 px-4 shadow-md"
      style={{ borderColor: getBorderColor(), borderStyle: "solid" }}
    >
      {renderMedalIcon()}
      <div className="flex flex-col justify-center w-full h-full">
        <div className="text-lg">
          {name}({participant})
        </div>
        <div className="text-sm text-neutral-500">{time}에 끝났쥐</div>
      </div>
      <div
        onClick={() => navigate(`/profile/room/game/result/${loungeId}/${gameId}`)}
        className="cursor-pointer"
      >
        <GameIcon className="w-12 h-12" />
      </div>
    </div>
  );
};

export default GameHistoryItem;
