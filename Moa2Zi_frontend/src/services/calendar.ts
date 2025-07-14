import apiClient from "@/services/http";
import { EMOTION } from "@/types/calendar";

interface GetCalendarTransaction {
  memberId: number | undefined;
  transactionDate: string | undefined;
}

// 달력 메인 리스트 가져오기
export const getCalendarTransaction = (params: GetCalendarTransaction) => {
  return apiClient.get("transactions/calender", { params });
};

// 달력 일자별 내역 가져오기
export const getCalendarDate = (params: GetCalendarTransaction) => {
  return apiClient.get("transactions/day", { params });
};

interface GetCommentsRequest {
  parentId: number | null;
  next: number | null;
  size: number;
}

interface GetCommentsTransaction {
  dayId: string | undefined;
  request: GetCommentsRequest;
}

// 달력 댓글 가져오기
export const getCalendarComments = (params: GetCommentsTransaction) => {
  return apiClient.get(`days/${params.dayId}/comments`, { params: params.request });
};

interface PostCommentsRequest {
  parentId: number | null;
  content: string;
}

interface PostCommentsTransaction {
  dayId: string | undefined;
  request: PostCommentsRequest;
}

// 달력 댓글 쓰기
export const postCalendarComment = (data: PostCommentsTransaction) => {
  return apiClient.post(`days/${data.dayId}/comments`, data.request);
};

interface PatchEmotionRequestBody {
  transactionId: number;
  emotion: EMOTION;
}

// 기분 등록하기
export const patchEmotion = (data: PatchEmotionRequestBody) => {
  return apiClient.patch("/transactions/emotion", data);
};
