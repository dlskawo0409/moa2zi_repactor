import { useNavigate } from "react-router-dom";
import { getProfileIcon } from "@/utils/getProfileIcon";
import GameIcon from "@components/svgs/GameIcon";
import { Badge } from "@components/ui/badge";
import { formatKoreanDate } from "@/utils/formatDate";

interface LoungeCardProps {
  lounge: {
    loungeId: number;
    title: string;
    loungeStatus: string;
    lastSendTime: string;
    participantList: { memberId: number; nickname: string; profileImage: string }[];
    createdAt: string;
    unReadMessageNum: number;
  };
}

const LoungeCard = ({ lounge }: LoungeCardProps) => {
  const navigate = useNavigate();

  let containerClass = "flex flex-col w-full gap-3 px-4 pc:px-5 py-4 rounded-lg cursor-pointer ";

  if (lounge.loungeStatus === "RUNNING") {
    containerClass += "bg-primary-400 hover:bg-primary-500";
  } else if (lounge.loungeStatus === "COMPLETED") {
    containerClass += "border-2 border-primary-400 hover:border-primary-500";
  } else if (lounge.loungeStatus === "TERMINATED") {
    containerClass += "border-2 border-neutral-300 hover:border-neutral-400 bg-neutral-200";
  }

  return (
    <div className={containerClass} onClick={() => navigate(`room/${lounge.loungeId}`)}>
      <div className="flex justify-between">
        <div className="flex gap-2 text-sm pc:text-md">
          <div>{lounge.title || "라운지 제목 없음"}</div>
          <div>({lounge.participantList.length})</div>
        </div>
        {lounge.lastSendTime ? (
          <div className="flex items-center text-xxs pc:text-xs">
            {formatKoreanDate(lounge.lastSendTime)}
          </div>
        ) : (
          <div className="flex items-center text-xxs pc:text-xs">
            {formatKoreanDate(lounge.createdAt)}
          </div>
        )}
      </div>
      <div className="flex items-center justify-between">
        <div className="flex items-center">
          {lounge.participantList.slice(0, 4).map((p, i) => {
            const ParticipantIcon = getProfileIcon(p.profileImage);
            return (
              <div
                key={p.memberId}
                className={`w-6 h-6 pc:w-7 pc:h-7 rounded-full overflow-hidden bg-white border ${
                  i !== 0 ? "-ml-2" : ""
                } flex items-center justify-center`}
                onClick={(e) => {
                  e.stopPropagation();
                  navigate(`/profile/friend/${p.memberId}`);
                }}
              >
                {ParticipantIcon ? <ParticipantIcon className="w-full h-full" /> : null}
              </div>
            );
          })}
          {lounge.participantList.length > 4 && (
            <div className="ml-2 text-xs pc:text-sm">+{lounge.participantList.length - 4}</div>
          )}
        </div>
        <div className="flex justify-center items-center gap-2">
          {lounge.loungeStatus === "RUNNING" && (
            <Badge className="bg-white text-neutral-800 gap-2 pc:gap-3 py-0.5 pc:py-1 hover:bg-primary-200 cursor-pointer">
              <GameIcon />
              <div className="text-xxs pc:text-xs">게임 진행중</div>
            </Badge>
          )}
          {lounge.loungeStatus !== "RUNNING" && lounge.unReadMessageNum > 0 && (
            <div className="flex bg-negative-500 text-white text-xxs pc:text-xs rounded-full w-4 pc:w-5 h-4 pc:h-5 justify-center items-center">
              {lounge.unReadMessageNum}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default LoungeCard;
