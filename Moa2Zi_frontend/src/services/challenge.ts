import apiClient from "@/services/http";

interface ChallengeQuery {
  type: "SEARCH" | "MINE" | "RECOMMEND" | "POPULAR";
  keyword?: string;
  tag?: string;
  status?: string;
  next?: number;
  participantCountNext?: number;
  size?: number;
}

export const getChallenges = async (query: ChallengeQuery) => {
  const { data } = await apiClient.get("/challenges", {
    params: query,
  });

  return data.challengeList;
};

export const postChallenge = async (challengeTimeId: number) => {
  await apiClient.post(`/challenges/${challengeTimeId}`);
};

interface ChallengeParticipantQuery {
  status?: string;
  next?: number;
  size?: number;
}

export const getChallengeParticipants = async (
  challengeId: number,
  query: ChallengeParticipantQuery,
) => {
  const { data } = await apiClient.get(`/challenges/${challengeId}/participants`, {
    params: query,
  });

  return data;
};

interface ChallengeReviewQuery {
  lastTime?: string;
  size?: number;
}

export const getChallengeReviews = async (challengeId: number, query: ChallengeReviewQuery) => {
  const { data } = await apiClient.get(`/challenges/reviews/${challengeId}`, {
    params: query,
  });

  return data.reviewList;
};

export const postChallengeReview = async (challengeTimeId: number, reviewText: string) => {
  const { data } = await apiClient.post(`/challenges/${challengeTimeId}/review`, {
    review: reviewText,
  });
  return data;
};

export const postChallengeReviewLike = async (challengeParticipantId: number) => {
  const { data } = await apiClient.post(`/challenges/reviews/${challengeParticipantId}`);
  return data;
};

export const getPrize = async (memberId: number) => {
  const response = await apiClient.get("/challenges/award", {
    params: {
      memberId,
    },
  });

  return response;
};

export const readPrize = async (challengeTimeId: number) => {
  const response = await apiClient.put(`/challenges/award/read?challengeTimeId=${challengeTimeId}`);

  return response;
};
