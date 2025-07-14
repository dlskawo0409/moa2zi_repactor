import apiClient from "@/services/http";

interface GetTransactionParams {
  memberId: number;
  categoryId?: number;
  transactionDate: number;
  merchantName?: string;
  accountNo?: string;
  cardNo?: string;
  transactionType?: string;
  paymentMethod?: string;
  emotion?: string;
  memo?: string;
  transactionTime?: string;
}

// 가계부 리스트 가져오기
export const getTransactions = async ({
  memberId,
  categoryId,
  transactionDate,
  merchantName,
  accountNo,
  cardNo,
  transactionType,
  paymentMethod,
  emotion,
  memo,
  transactionTime,
}: GetTransactionParams) => {
  const response = await apiClient.get("/transactions", {
    params: {
      memberId,
      categoryId,
      transactionDate,
      merchantName,
      accountNo,
      cardNo,
      transactionType,
      paymentMethod,
      emotion,
      memo,
      transactionTime,
    },
  });

  return response.data;
};

interface PostTransactionParams {
  categoryId: number | undefined;
  transactionDate: number;
  transactionBalance: number;
  transactionType: string;
  paymentType: string | null;
  emotion?: string;
  memo?: string;
  transactionTime: string;
  merchantName: string;
  isInBudget?: boolean;
}

// 가계부 수기입력
export const postTransactions = async ({
  categoryId,
  transactionDate,
  transactionBalance,
  transactionType,
  paymentType,
  emotion,
  memo,
  transactionTime,
  merchantName,
  isInBudget,
}: PostTransactionParams) => {
  const response = await apiClient.post("/transactions", {
    categoryId,
    transactionDate,
    transactionBalance,
    transactionType,
    paymentType,
    emotion,
    memo,
    transactionTime,
    merchantName,
    isInBudget,
  });

  return response.data;
};

interface GetTransactionSumParams {
  categoryId?: number;
  transactionDate: number;
  unitCount?: number;
  accountNo?: string;
  cardNo?: string;
  transactionType?: string;
  paymentMethod?: string;
  emotion?: string;
  memo?: string;
  isAscending?: boolean;
}

// 총 지출 금액 가져오기
export const getTransactionsSum = async ({
  categoryId,
  transactionDate,
  unitCount,
  accountNo,
  cardNo,
  transactionType,
  paymentMethod,
  emotion,
  memo,
  isAscending,
}: GetTransactionSumParams) => {
  const response = await apiClient.get("/transactions/sum", {
    params: {
      categoryId,
      transactionDate,
      unitCount,
      accountNo,
      cardNo,
      transactionType,
      paymentMethod,
      emotion,
      memo,
      isAscending,
    },
  });

  return response.data;
};
