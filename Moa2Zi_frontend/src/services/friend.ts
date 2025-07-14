import apiClient from "@/services/http";

interface GetFriendParams {
  requestId?: number;
  acceptId?: number;
  status?: string;
  next?: number | null;
  size?: number;
}

// 친구 목록 가져오기
export const getFriendList = async ({
  requestId,
  acceptId,
  status,
  next,
  size = 10,
}: GetFriendParams) => {
  const response = await apiClient.get("/friends", {
    params: {
      requestId,
      acceptId,
      status,
      next,
      size,
    },
  });

  return response.data;
};

// 친구 요청 보내기
export const postFriend = async (acceptId: number) => {
  const response = await apiClient.post(`/friends/${acceptId}`);

  return response.data;
};

// 친구 상태 변경
export const putFriend = async (friendId: number) => {
  const response = await apiClient.put(`/friends/${friendId}`);

  return response.data;
};

// 친구 관계 취소
export const deleteFriend = async (friendId?: number) => {
  const response = await apiClient.delete(`/friends/${friendId}`);

  return response.data;
};

// 친구 회원정보 가져오기
export const getFriendInfo = async (memberId?: string) => {
  const response = await apiClient.get(`members/${memberId}`);

  return response.data;
};
