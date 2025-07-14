import { useMutation, useQueryClient } from "@tanstack/react-query";
import { postLogin } from "@/services/auth";
import { LoginRequest } from "@/types/auths";

export const useLogin = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: LoginRequest) => postLogin(data),
    onSuccess: (response) => {
      const token = response.headers.access;
      queryClient.setQueryData(["accessToken"], token);
    },
  });
};
