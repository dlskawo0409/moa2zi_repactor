import { useState, MouseEvent } from "react";
import { getChallengeParticipants } from "@/services/challenge";
import { ChallengeCardProps } from "@/utils/challengeMapper";
import ParticipantDrawer from "@/components/challenge/ParticipantDrawer";

const fetchParticipants = async (challengeId: number, page: number) => {
  const PAGE_SIZE = 20;
  const data = await getChallengeParticipants(challengeId, {
    status: "",
    size: PAGE_SIZE,
  });
  return {
    participants: data.participants,
    total: data.total,
    size: data.size,
    hasNext: data.hasNext,
    next: data.next,
  };
};

const ChallengeCardRight = ({
  challenge,
  onJoinChallenge,
}: {
  challenge: ChallengeCardProps;
  onJoinChallenge: (challengeTimeId: number) => Promise<boolean>;
}) => {
  const [joined, setJoined] = useState<boolean>(false);

  const handleJoin = async (e: MouseEvent) => {
    e.stopPropagation();
    const result = await onJoinChallenge(challenge.challengeTimeId);
    if (result) {
      setJoined(true);
    } else {
      setJoined(false);
    }
  };

  const isOngoing = challenge.status === "ONGOING" || joined;

  return (
    <>
      {/* {joined ? (
        <Toaster position="top-center" toastOptions={{ className: "custom-toast-positive" }} />
      ) : (
        <Toaster position="top-center" toastOptions={{ className: "custom-toast-negative" }} />
      )} */}
      <div className="flex flex-col gap-3 justify-between items-between w-[65%] h-full py-2">
        <div className="flex items-start gap-2">
          <div className="pc:text-lg text-sm font-semibold flex items-center gap-1 min-w-0">
            <span className="flex-1 min-w-0 mr-14 truncate">{challenge.title}</span>
            {isOngoing && (
              <span className="absolute right-6 top-4 bg-primary-500 text-white pc:text-sm text-xs px-2 py-0.5 rounded-full whitespace-nowrap">
                참여중
              </span>
            )}
            {challenge.status === "SUCCESS" && (
              <span className="absolute right-6 top-4 bg-green-400 text-white text-sm px-2 py-0.5 ml-2 rounded-full whitespace-nowrap">
                성공
              </span>
            )}
          </div>
        </div>
        <div className="pc:text-sm text-xs w-full text-gray-600 space-x-2 whitespace-nowrap overflow-auto">
          {challenge.tags.map((tag, tagIdx) => (
            <span key={tagIdx} className="text-gray-400">
              #{tag}
            </span>
          ))}
        </div>
        <div className="text-xs text-gray-400">
          시작일자 : {new Date(challenge.startTime).toLocaleString().slice(0, -3)}
          <br />
          종료일자 : {new Date(challenge.endTime).toLocaleString().slice(0, -3)}
        </div>
        <div className="flex w-full justify-between items-center">
          <div
            className="flex items-center w-full justify-between"
            onClick={(e) => e.stopPropagation()}
          >
            <ParticipantDrawer
              fetchPage={(page) => fetchParticipants(challenge.challengeId, page)}
              participantCount={challenge.participantCount}
            />

            {!isOngoing && challenge.status !== "SUCCESS" ? (
              <button
                onClick={handleJoin}
                className="pc:text-sm text-xxs text-white bg-primary-500 pc:px-3 px-2 py-1 rounded-full hover:bg-orange-500 transition"
              >
                참여하기
              </button>
            ) : (
              <span className="pc:text-sm text-xxs text-gray-400 bg-gray-100 pc:px-3 px-2 py-1 rounded-full cursor-default">
                참여완료
              </span>
            )}
          </div>
        </div>
      </div>
    </>
  );
};

export default ChallengeCardRight;
