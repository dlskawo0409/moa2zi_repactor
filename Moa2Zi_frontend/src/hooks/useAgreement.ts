import { useQueryClient } from "@tanstack/react-query";

const AGREEMENT_KEY = ["agreement"];

export const useAgreement = () => {
  const queryClient = useQueryClient();

  const setAgreement = (agreed: boolean) => {
    queryClient.setQueryData(AGREEMENT_KEY, agreed);
  };

  const getAgreement = (): boolean => {
    return queryClient.getQueryData(AGREEMENT_KEY) ?? false;
  };

  return { setAgreement, getAgreement };
};
