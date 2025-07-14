export interface FriendInfo {
  friendId: number;
  memberId: number;
  nickname: string;
  profileImage: string;
}

export interface SearchMember {
  friendsOrder: number;
  memberId: number;
  nickname: string;
  profileImage: string;
}
