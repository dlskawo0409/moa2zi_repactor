import { useQuery } from "@tanstack/react-query";
import { getUserInfo } from "@/services/auth";
import { UserInfo } from "@/types/auths";

export const useUserInfo = () => {
  return useQuery<UserInfo, Error>({
    queryKey: ["userInfo"],
    queryFn: getUserInfo,
    staleTime: 0, // 쿼리가 항상 신선하게 유지되도록 설정
    refetchOnWindowFocus: true, // 사용자가 화면을 다시 포커스할 때마다 리페치
    refetchOnReconnect: true, // 네트워크가 재연결될 때마다 리페치
  });
};
