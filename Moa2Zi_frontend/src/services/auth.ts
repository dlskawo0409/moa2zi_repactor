import apiClient from "./http";
import { SignupRequest, LoginRequest, UserInfo } from "@/types/auths";

// 약관 전체조회
export const getTerms = () => {
  return apiClient.get("/terms", { headers: { auth: false } });
};

// 약관 전체조회
export const getTermDetails = (termId: string | undefined) => {
  return apiClient.get(`/terms/${termId}/details`, { headers: { auth: false } });
};

// 이메일 중복 확인
export const postEmailCheck = (email: string | undefined) => {
  return apiClient.post("/members/check-email", { username: email }, { headers: { auth: false } });
};

// 닉네임 중복 확인
export const postNicknameCheck = (nickname: string | undefined) => {
  return apiClient.post(
    "/members/check-nickname",
    { nickname: nickname },
    { headers: { auth: false } },
  );
};

// 회원가입
export const postSignup = (data: SignupRequest) => {
  return apiClient.post("/members", data, { headers: { auth: false } });
};

// 로그인
export const postLogin = (data: LoginRequest) => {
  return apiClient.post("/login", data, { headers: { auth: false } });
};

// 로그아웃
export const postLogout = () => {
  return apiClient.post("/logout");
};

// 본인 정보 확인
export const getUserInfo = async (): Promise<UserInfo> => {
  const response = await apiClient.get("/members");
  return response.data;
};

// 인증번호 받기
export const postPhoneNumber = async (phoneNumber: string) => {
  const response = await apiClient.post(
    "/sms",
    { phoneNumber: phoneNumber },
    { headers: { auth: false } },
  );
  return response.data;
};

// 인증번호 검증하기
export const postVerificationNumber = async (phoneNumber: string, validateNum: string) => {
  const response = await apiClient.post(
    "/sms/validate",
    { phoneNumber: phoneNumber, validateNum: validateNum },
    { headers: { auth: false } },
  );
  return response.data;
};
