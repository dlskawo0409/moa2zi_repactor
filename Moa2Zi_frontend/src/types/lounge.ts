export interface Participant {
  memberId: number;
  nickname: string;
  profileImage: string;
}

export type LoungeStatus = "RUNNING" | "COMPLETED" | "TERMINATED";

export interface Lounge {
  loungeId: number;
  title: string;
  loungeStatus: LoungeStatus;
  lastSendTime: string;
  unReadMessageNum: number;
  participantList: Participant[];
  createdAt: string;
}

export interface LoungeList {
  loungeList: Lounge[];
  unReadNumSum: number;
  total: number;
  size: number;
  hasNext: boolean;
  next: number | null;
  loungeStatus: LoungeStatus;
}

export interface Member {
  memberId: number;
  username: string;
  nickname: string;
  profileImage: string;
  createdAt: string;
}

export interface Friend {
  friendId: number;
  updatedAt: string;
  requestMember: Member;
  acceptMember: Member;
}

export interface FriendList {
  friendList: Friend[];
  total: number;
  size: number;
  hasNext: boolean;
  next: number | null;
}

export interface LoungeInfo {
  loungeId: number;
  title: string;
  loungeStatus: LoungeStatus;
  participantList: Participant[];
}

export type GameStatus = "RUNNING" | "COMPLETED";

export interface GameInfo {
  gameId: number;
  loungeId: number;
  gameStatus: GameStatus;
  totalMember: number;
  solvedMember: number;
  createdAt: string;
  endTime: string;
  nextQuizId: number;
}

export type QuizAnswer = "YES" | "NO";

export interface Quiz {
  id: number;
  gameId: number;
  context: string;
  answer: QuizAnswer;
}

export interface LoungeWithGame {
  createdAt: string;
  duration: number;
  gameEndTime: string;
  id: number;
  loungeEndTime: string;
  title: string;
}

export interface PostQuizAnswerRequest {
  quizId: string | undefined;
  submission: QuizAnswer;
}

export interface QuizResult {
  quizId: string;
  content: string;
  isCorrect: boolean;
  submittedAnswer: QuizAnswer;
}

export interface QuizRank {
  correctCount: number;
  memberId: number;
  nickname: string;
}

export type MessageType = "CHAT" | "OUT" | "TIME" | "IMAGE";

export interface ChatItem {
  chatId: string | number;
  content: string;
  loungeId: string | undefined;
  memberId: number;
  messageType: MessageType;
  nickname: string;
  profileImage: string;
  timeStamp: string | number;
}

export interface ChatData {
  chatList: ChatItem[];
  total: number;
  size: number;
  hasNext: boolean;
  next: string;
}

export interface ChatMessage {
  loungeId: string;
  memberId: number;
  messageType: MessageType;
  timeStamp: string;
  content: string;
  nickname: string;
  profileImage: string;
}
