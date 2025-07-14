import apiClient from "@/services/http";

export const getPocketMonies = async () => {
  const response = await apiClient.get("/pocket-monies");

  return response.data;
};

interface PostPocketMoniesParams {
  totalAmount: number;
  thisMonthHave: boolean;
}

export const postPocketMonies = async ({ totalAmount, thisMonthHave }: PostPocketMoniesParams) => {
  const response = await apiClient.post("/pocket-monies", {
    totalAmount,
    thisMonthHave,
  });

  return response.data;
};
