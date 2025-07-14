import { useNavigate, useParams } from "react-router-dom";
import CommonButton from "@components/common/CommonButton";
import GameIcon from "@components/svgs/GameIcon";

const GameFinishedModal = () => {
  const navigate = useNavigate();
  const { gameId, loungeId } = useParams<{
    gameId: string;
    loungeId: string;
  }>();

  return (
    <div className="fixed inset-0 z-40 bg-neutral-500/80 max-w-[600px] mx-auto flex items-center justify-center">
      <div className="flex flex-col items-center justify-center p-4 gap-5 bg-white w-64 h-36 rounded-lg">
        <p className="mb-4 font-semibold">퀴즈를 모두 풀었어요!</p>
        <CommonButton
          variant="primary"
          className="w-full"
          onClick={() => navigate(`/lounge/room/game/result/${loungeId}/${gameId}`)}
        >
          <GameIcon />내 점수 보기
        </CommonButton>
      </div>
    </div>
  );
};

export default GameFinishedModal;
