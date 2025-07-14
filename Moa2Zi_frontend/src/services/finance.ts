import apiClient from "@/services/http";

import { bankList } from "@/constants/bankList";
import { cardIssuerList } from "@/constants/cardIssuerList";

// 은행/카드사 목록 조회
export const getAssets = async () => {
  return {
    bankList,
    cardIssuerList,
  };
};

interface PostAssetsParams {
  bankCodeList: string[];
  cardIssuerCodeList: string[];
}

// 연결할 은행/카드사 전송
export const postAssets = async ({ bankCodeList, cardIssuerCodeList }: PostAssetsParams) => {
  const response = await apiClient.post("/fin/assets", { bankCodeList, cardIssuerCodeList });

  return response.data;
};

// 1원 송금 요청
export const postAccountAuth = async (accountNo: string) => {
  const response = await apiClient.post("/fin/accountAuth", { accountNo, authText: "모앗쥐" });

  return response.data;
};

// 1원 송금 검증
export const postAccountAuthCheck = async (accountNo: string, authCode: string) => {
  const response = await apiClient.post("/fin/accountAuth/check", {
    accountNo,
    authText: "모앗쥐",
    authCode,
  });

  return response.data;
};

// 탐색된 자산 조회
export const getAssetsFetch = async () => {
  const response = await apiClient.get("/fin/assets/fetch");

  return response.data;
};

// 연결된 계좌 조회
export const getMemberAccounts = async () => {
  const response = await apiClient.get("/fin/member/accounts");

  return response.data;
};

// 연결된 카드 조회
export const getMemberCards = async (memberId: number) => {
  const response = await apiClient.get("/fin/admin/creditCard", { params: { memberId } });

  return response.data;
};
