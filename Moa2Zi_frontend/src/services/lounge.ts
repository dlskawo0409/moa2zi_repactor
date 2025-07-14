import apiClient from "@/services/http";
import { LoungeStatus } from "@/types/lounge";
import { QuizAnswer } from "@/types/lounge";

interface GetLoungeListParams {
  searchType: string;
  keyword?: string;
  next?: number | null;
  size?: number;
  loungeStatus?: LoungeStatus | null;
}

// 라운지 목록 불러오기
export const getLoungeList = (params: GetLoungeListParams) => {
  return apiClient.get("/lounges", {
    params,
  });
};

interface GetFriendListParams {
  requestId?: number;
  acceptId?: number;
  status?: string;
  next?: number | null;
  size: number;
}

// 라운지 시작을 위한 친구 목록 가져오기
export const getFriendList = (params: GetFriendListParams) => {
  return apiClient.get("/friends", {
    params,
  });
};

interface PostLoungeRequestBody {
  title: string;
  participantList: number[];
  endTime: string;
  duration: string;
}

// 라운지 시작하기
export const postLounge = (data: PostLoungeRequestBody) => {
  return apiClient.post("/lounges", data);
};

// 라운지 참여 멤버 가져오기
export const getLoungeMembers = (loungeId: string | undefined) => {
  return apiClient.get(`/lounges/${loungeId}/details`);
};

// 라운지 게임 목록 가져오기
export const getGameList = (loungeId: string | undefined) => {
  return apiClient.get("/games", {
    params: {
      loungeId,
    },
  });
};

// 라운쥐 퀴즈 가져오기
export const getQuiz = (quizId: string | undefined) => {
  return apiClient.get(`quiz/${quizId}`);
};

interface PostQuizAnswerRequestBody {
  quizId: string | undefined;
  submission: QuizAnswer;
}

// 라운쥐 문제 풀기
export const postQuizAnswer = (data: PostQuizAnswerRequestBody) => {
  return apiClient.post("/answers", data);
};

interface GetGameResultParams {
  memberId: number | undefined;
  gameId: string | undefined;
}

// 라운쥐 결과 가져오기
export const getGameResult = (params: GetGameResultParams) => {
  return apiClient.get("quiz/result", { params });
};

// 라운쥐 랭킹 가져오기
export const getGameRanking = (params: GetGameResultParams) => {
  return apiClient.get("quiz/ranking", { params });
};

interface getChatListParams {
  loungeId: string | undefined;
  next?: string | undefined;
  size?: number;
}

// 채팅 목록 가져오기
export const getChatList = (params: getChatListParams) => {
  return apiClient.get("/chat", { params });
};

interface PutChatReadRequestBody {
  loungeId: string | undefined;
  lastReadTime: string;
}

// 채팅 읽음 처리
export const putChatRead = (data: PutChatReadRequestBody) => {
  return apiClient.put("/chat/check", data);
};
