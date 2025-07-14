export type ChallengeCardProps = {
  challengeId: number;
  challengeTimeId: number;
  challengeParticipantId: number;
  title: string;
  unit: string;
  tags: string[];
  categoryName: string;
  remainingTime: string | null;
  participantCount: number;
  status: string;
  progress: number;
  isParticipating: boolean;
  startTime: string;
  endTime: string;
};

export const convertToChallengeCardProps = (apiData: any): ChallengeCardProps => {
  return {
    challengeId: apiData.challengeId,
    challengeTimeId: apiData.challengeTimeId,
    challengeParticipantId: apiData.challengeParticipantId,
    title: apiData.descriptionMessage,
    unit: apiData.unit,
    tags: apiData.tags ? apiData.tags.split(",") : [],
    categoryName: apiData.categoryName,
    remainingTime: getRemainingTime(apiData.endTime),
    participantCount: apiData.participantCount,
    status: apiData.status,
    progress: apiData.progress,
    isParticipating: !!apiData.challengeParticipantId,
    startTime: apiData.startTime,
    endTime: apiData.endTime,
  };
};

const getRemainingTime = (endTime: string) => {
  const now = new Date();
  const end = new Date(endTime);
  const diff = end.getTime() - now.getTime();

  if (diff < 0) return null;

  const days = Math.floor(diff / (1000 * 60 * 60 * 24));
  const hours = Math.floor((diff / (1000 * 60 * 60)) % 24);
  const minutes = Math.floor((diff / (1000 * 60)) % 60);

  return `${days}일 ${hours}시간 ${minutes}분`;
};
