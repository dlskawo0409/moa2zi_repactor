import { GameInfo } from "@/types/lounge";
import { Badge } from "@components/ui/badge";
import GameIcon from "@components/svgs/GameIcon";
import CommonButton from "@components/common/CommonButton";

const getStatusStyles = (status: string) => {
  switch (status) {
    case "RUNNING":
      return {
        badgeColor: "bg-primary-500 hover:bg-primary-400",
        buttonText: "게임 진행하기",
        borderColor: "border-4 border-primary-500",
        backgroundColor: "bg-white",
      };
    case "COMPLETED":
      return {
        badgeColor: "bg-neutral-400 hover:bg-neutral-300",
        buttonText: "게임 결과보기",
        borderColor: "border-4 border-neutral-50",
        backgroundColor: "bg-neutral-50",
      };
    default:
      return {
        badgeColor: "bg-gray-300",
        buttonText: "게임 정보보기",
        borderColor: "border-4 border-gray-50",
        backgroundColor: "bg-gray-100",
      };
  }
};

interface LoungeChatGameListItemProps {
  game: GameInfo;
  onClick: (status: string, gameId: number, quizId: number) => void;
}

const LoungeChatGameListItem = ({ game, onClick }: LoungeChatGameListItemProps) => {
  const styles = getStatusStyles(game.gameStatus);

  return (
    <div
      className={`flex flex-col w-1/2 p-5 gap-10 ${styles.borderColor} ${styles.backgroundColor}`}
    >
      <div className="flex flex-col gap-2">
        <div className="flex justify-between">
          <Badge className={`rounded-full ${styles.badgeColor}`}>
            {game.gameStatus === "RUNNING" ? "진행중" : "진행 완료"}
          </Badge>
          <div className="text-xs pc:text-sm">
            {game.solvedMember}/{game.totalMember}
          </div>
        </div>
        <div className="flex gap-2 text-sm pc:text-md">
          <div>시작: </div>
          <div>{new Date(game.createdAt).toLocaleDateString()}</div>
        </div>
        <div className="flex gap-2 text-sm pc:text-md">
          <div>결과: </div>
          <div>{new Date(game.endTime).toLocaleDateString()}</div>
        </div>
      </div>
      <CommonButton
        variant="shadow"
        className="text-xs pc:text-sm h-7 pc:h-9"
        onClick={() => onClick(game.gameStatus, game.gameId, game.nextQuizId)}
      >
        <GameIcon />
        <div>{styles.buttonText}</div>
      </CommonButton>
    </div>
  );
};

export default LoungeChatGameListItem;
