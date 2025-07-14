import apiClient from "@/services/http";

interface getCategoryStatisticsParams {
  transactionDate: number;
  unitCount: number;
  unitRankCount?: number;
  accountNo?: string;
  cardNo?: string;
  paymentMethod?: string;
  emotion?: string;
  memo?: string;
  isDescending?: boolean;
}

// 카테고리별 분석 가져오기
export const getCategoryStatistics = async ({
  transactionDate,
  unitCount,
  unitRankCount,
  accountNo,
  cardNo,
  paymentMethod,
  emotion,
  memo,
  isDescending,
}: getCategoryStatisticsParams) => {
  const response = await apiClient.get("/transactions/sum/category", {
    params: {
      transactionDate,
      unitCount,
      unitRankCount,
      accountNo,
      cardNo,
      paymentMethod,
      emotion,
      memo,
      isDescending,
    },
  });

  return response.data;
};

// Yono Point 가져오기
export const getYonoPoints = async (transactionDate: number) => {
  const response = await apiClient.get("/yono-points", {
    params: {
      transactionDate,
    },
  });

  return response.data;
};
