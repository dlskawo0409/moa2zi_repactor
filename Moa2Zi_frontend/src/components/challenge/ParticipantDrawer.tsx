import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getProfileIcon } from "@/utils/getProfileIcon";
import { Skeleton } from "@/components/ui/skeleton";
import CommonDrawer from "@/components/common/CommonDrawer";

export type Participant = {
  challengeParticipantId: number;
  memberId: number;
  nickname: string;
  profileImage: string;
  gender: "MALE" | "FEMALE";
};

interface ParticipantDrawerProps {
  fetchPage: (page: number) => Promise<{
    participants: Participant[];
    total: number;
    size: number;
    hasNext: boolean;
    next: number;
  }>;
  participantCount: number;
}

const ParticipantDrawer = ({ fetchPage, participantCount }: ParticipantDrawerProps) => {
  const [participants, setParticipants] = useState<Participant[]>([]);
  const [page, setPage] = useState<number>(0);
  const [hasNext, setHasNext] = useState<boolean>(true);
  const [isLoading, setIsLoading] = useState<boolean>(false);

  const scrollContainerRef = useRef<HTMLDivElement | null>(null);
  const navigate = useNavigate();

  // 페이지 불러오기
  const loadMore = async () => {
    if (!hasNext || isLoading) return; // 이미 로딩 중이거나 더 이상 불러올 페이지가 없으면 종료

    setIsLoading(true);
    const res = await fetchPage(page);

    setParticipants((prev) => {
      // 기존 참여자 목록에 새로 불러온 참여자를 추가
      const existingIds = new Set(prev.map((p) => p.challengeParticipantId));
      const newOnes = (res.participants ?? []).filter(
        (p) => !existingIds.has(p.challengeParticipantId),
      );
      return [...prev, ...newOnes];
    });

    setHasNext(res.hasNext);
    setPage((prev) => prev + 1);
    setIsLoading(false);
  };

  // 초기 1페이지 불러오기
  useEffect(() => {
    loadMore();
  }, []);

  const handleScroll = () => {
    const container = scrollContainerRef.current;
    if (!container || isLoading || !hasNext) return;

    const { scrollTop, scrollHeight, clientHeight } = container;
    if (scrollTop + clientHeight >= scrollHeight - 20) {
      loadMore();
    }
  };

  return (
    <CommonDrawer
      trigger={
        <div className="flex items-center cursor-pointer" onClick={(e) => e.stopPropagation()}>
          {participants.slice(0, 3).map((p, i) => {
            const Icon = getProfileIcon(p.profileImage);
            return (
              <div
                key={`preview-${p.challengeParticipantId}`}
                className={`w-6 h-6 pc:w-7 pc:h-7 rounded-full overflow-hidden bg-white border border-neutral-300 ${
                  i !== 0 ? "-ml-2" : ""
                } flex items-center justify-center`}
              >
                {Icon && <Icon className="w-full h-full" />}
              </div>
            );
          })}
          {/* {participants.length >= 3 ? (
            <div className="flex items-center pc:gap-3">
              <div className="ml-1 text-xxs pc:text-sm text-neutral-500">
                +{participants.length - 3}명 참여중
              </div>
            </div>
          ) : participants.length != 0 ? (
            <div className="flex items-center">
              <div className="pc:ml-3 ml-1 pc:text-sm text-xs text-gray-500">
                {participantCount}명 참여중
              </div>
            </div>
          ) : ( */}
          <div className="flex items-center pc:ml-2 ml-1">
            <div className="pc:text-sm text-xs text-gray-500">{participantCount}명 참여중</div>
          </div>
        </div>
      }
      header={<div className="text-lg font-semibold">참여자 목록</div>}
    >
      <div
        ref={scrollContainerRef}
        onScroll={handleScroll}
        className="flex flex-col gap-4 px-4 py-4 max-h-[60vh] overflow-y-auto"
      >
        {participants.map((p) => {
          const Icon = getProfileIcon(p.profileImage);
          return (
            <div
              key={p.challengeParticipantId}
              className="flex items-center gap-4 cursor-pointer"
              onClick={() => navigate(`/profile/friend/${p.memberId}`)}
            >
              <div className="w-8 h-8 rounded-full overflow-hidden bg-white border border-neutral-300 ">
                {Icon && <Icon className="w-full h-full" />}
              </div>
              <div className="text-md">{p.nickname}</div>
            </div>
          );
        })}

        {isLoading && (
          <div className="flex items-center gap-4">
            <Skeleton>
              <div className="w-8 h-8 rounded-full overflow-hidden"></div>
            </Skeleton>
            <Skeleton>
              <div className="w-20 h-4"> </div>
            </Skeleton>
          </div>
        )}
        {!hasNext && !isLoading && (
          <div className="text-sm text-gray-400 text-center">모든 참여자를 불러왔습니다.</div>
        )}
      </div>
    </CommonDrawer>
  );
};

export default ParticipantDrawer;
