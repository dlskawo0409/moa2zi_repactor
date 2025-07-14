import apiClient from "@/services/http";

// 게임 목록 가져오기
export const getGamesHistories = async () => {
  const response = await apiClient.get("/games/histories");

  return response.data;
};
