export interface MemberTerm {
  termId: number;
  agree: boolean;
}

export interface SignupRequest {
  username: string;
  password: string;
  nickname: string;
  birthday: string;
  gender: "MALE" | "FEMALE";
  profileImage: string;
  phoneNumber: string;
  memberTermList: { termId: number; agree: boolean }[];
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface UserInfo {
  alarm: boolean;
  birthday: string;
  createdAt: string;
  disclosure: string;
  gender: string;
  memberId: number;
  nickname: string;
  profileImage: string;
  updateAt: string;
  username: string;
  theyAreFriend: boolean;
}
