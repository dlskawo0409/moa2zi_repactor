import axios from "axios";
import { queryClient } from "@/lib/queryClient";
import { useAuthStore } from "@/stores/useAuthStore";

type ReissueOptions = {
  redirectOnFail?: boolean;
  redirectOnSuccess?: boolean;
};

export const reissueToken = async (options?: ReissueOptions): Promise<string | null> => {
  let isRefreshing = false; // 중복 요청 방지를 위한 플래그. 현재 토큰 갱신 중인지 여부.
  let refreshPromise: Promise<string | null> | null = null; // 이미 실행 중인 갱신 요청이 있다면 그 결과를 기다리기 위해 사용.

  if (isRefreshing && refreshPromise) return refreshPromise;

  isRefreshing = true;
  refreshPromise = (async () => {
    try {
      const response = await axios.post(
        "https://j12a403.p.ssafy.io/api/v1/reissue",
        {},
        { withCredentials: true },
      );

      const newToken = response.headers.access;

      if (newToken) {
        queryClient.setQueryData(["accessToken"], newToken);
        useAuthStore.getState().setIsLoggedIn(true);

        if (options?.redirectOnSuccess) {
          // window.location.href = "/account-book/calendar";
        }

        return newToken;
      }

      return null;
    } catch {
      useAuthStore.getState().setIsLoggedIn(false);
      if (options?.redirectOnFail !== false) {
        // window.location.href = "/login";
      }
      return null;
    } finally {
      isRefreshing = false;
      refreshPromise = null;
    }
  })();

  return refreshPromise;
};
