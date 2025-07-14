import { bankList } from "@/constants/bankList";
import { cardIssuerList } from "@/constants/cardIssuerList";

// 은행 아이콘 & 이름 가져오기
export const getBankInfo = (bankCode: string) => {
  const bank = bankList.find((b) => b.bankCode === bankCode);
  return {
    name: bank?.bankName ?? "",
    Icon: bank?.Icon ?? null,
  };
};

// 카드사 아이콘 & 이름 가져오기
export const getCardIssuerInfo = (cardCode: string) => {
  const card = cardIssuerList.find((c) => c.cardIssuerCode === cardCode);
  return {
    name: card?.cardIssuerName ?? "",
    Icon: card?.Icon ?? null,
  };
};
