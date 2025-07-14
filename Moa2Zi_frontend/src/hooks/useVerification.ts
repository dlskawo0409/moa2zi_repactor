import { useQueryClient } from "@tanstack/react-query";

const VERIFICATION_KEY = ["verification"];

export const useVerification = () => {
  const queryClient = useQueryClient();

  const setVerified = (value: boolean) => {
    queryClient.setQueryData(VERIFICATION_KEY, value);
  };

  const getVerified = (): boolean => {
    return queryClient.getQueryData(VERIFICATION_KEY) ?? false;
  };

  return { setVerified, getVerified };
};
