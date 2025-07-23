import axios from "axios";
import createAuthRefreshInterceptor from "axios-auth-refresh";
import { queryClient } from "@/lib/queryClient";
import { reissueToken } from "@/lib/reissueToken";

const apiClient = axios.create({
  baseURL: "https://moa2zi.com/api/v1",
  withCredentials: true,
});

// 요청 전 인터셉터: 토큰이 없으면 먼저 reissue 시도
apiClient.interceptors.request.use(async (config) => {
  const requiresAuth = config?.headers?.auth !== false;

  if (requiresAuth) {
    let token = queryClient.getQueryData<string | null>(["accessToken"]);

    if (!token) {
      token = await reissueToken({ redirectOnFail: true });

      if (!token) {
        return Promise.reject(new Error("로그인 필요"));
      }
    }

    if (config.headers) {
      config.headers["access"] = token;
    }
  }

  // auth: false 헤더 제거
  if (config.headers && "auth" in config.headers) {
    delete config.headers.auth;
  }

  return config;
});

// 요청 실패 시 자동 토큰 재발급 로직 (401/403 응답)
const refreshAuthLogic = async (failedRequest: any) => {
  const newToken = await reissueToken();

  if (newToken) {
    failedRequest.config.headers["access"] = newToken;
    return Promise.resolve(); // 원래 요청 재시도
  }

  return Promise.reject(new Error("토큰 재발급 실패"));
};

// axios-auth-refresh 적용
createAuthRefreshInterceptor(apiClient, refreshAuthLogic);

export default apiClient;
