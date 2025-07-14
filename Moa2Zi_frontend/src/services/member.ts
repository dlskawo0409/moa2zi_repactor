import apiClient from "@/services/http";

interface GetMemberParams {
  nickname: string;
  friendsOrder?: number | null;
  next?: number | null;
  size: number;
}

// 친구 검색 목록 가져오기
export const getMembers = async ({ nickname, friendsOrder, next, size }: GetMemberParams) => {
  const response = await apiClient.get("/members/nickname", {
    params: {
      nickname,
      friendsOrder,
      next,
      size,
    },
  });

  return response.data;
};

export const updateMemberProfile = async (data: {
  nickname: string | undefined;
  birthday: string;
  gender: "MALE" | "FEMALE";
  profileImage: string | undefined;
  alarm: boolean;
  disclosure: "ALL" | "FRIEND" | "ONLY_ME";
}) => {
  return await apiClient.put("/members", data);
};
