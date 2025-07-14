import { useState } from "react";
import { useNavigate } from "react-router-dom";
import CommonButton from "@/components/common/CommonButton";
import { postFriend, putFriend, deleteFriend } from "@/services/friend";
import { getProfileIcon } from "@/utils/getProfileIcon";

interface FriendItemProps {
  friendId?: number;
  memberid: number;
  type: string;
  profileIcon: string;
  nickname: string;
  onAcceptSuccess?: (memberid: number) => void;
  onDeleteSuccess?: (memberid: number) => void;
}

const FriendItem = ({
  friendId,
  memberid,
  type,
  profileIcon,
  nickname,
  onAcceptSuccess,
  onDeleteSuccess,
}: FriendItemProps) => {
  const navigate = useNavigate();
  const Icon = getProfileIcon(profileIcon);

  const [isRequested, setIsRequested] = useState<boolean>(false);

  // 친구 프로필 보기
  const handleViewProfile = () => {
    navigate(`/profile/friend/${memberid}`);
  };

  // 친구 요청
  const handleRequestFriend = async () => {
    try {
      await postFriend(memberid);

      setIsRequested(true);
    } catch (error) {
      // console.error("친구 요청 실패:", error);
      alert("친구 요청에 실패했어요.");
    }
  };

  // 친구 수락
  const handleAcceptFriend = async () => {
    try {
      await putFriend(friendId || 0);
      onAcceptSuccess?.(friendId || 0);
    } catch (error) {
      alert("친구 수락에 실패했어요.");
    }
  };

  // 친구 삭제 & 친구 거절
  const handleDeleteFriend = async () => {
    try {
      await deleteFriend(friendId || 0);
      onDeleteSuccess?.(friendId || 0);
    } catch (error) {
      // console.error("친구 삭제 실패:", error);
      alert("친구 삭제에 실패했어요.");
    }
  };

  return (
    <div className="flex mx-5 justify-between">
      <div className="flex gap-4 cursor-pointer pe-2" onClick={handleViewProfile}>
        <div className="w-12 h-12 shrink-0">
          {Icon && <Icon className="w-full h-full border rounded-full" />}
        </div>
        <div className="flex items-center font-bold ">{nickname}</div>
      </div>
      {type === "friend" && (
        <div className="flex items-center gap-2">
          <CommonButton variant="positive" className="pc:px-10" onClick={handleViewProfile}>
            프로필 보기
          </CommonButton>
          <CommonButton variant="negative" className="pc:px-10" onClick={handleDeleteFriend}>
            친구 삭제
          </CommonButton>
        </div>
      )}
      {type === "request" && (
        <div className="flex items-center gap-2">
          <CommonButton variant="negative" className="pc:px-10" onClick={handleDeleteFriend}>
            요청 취소
          </CommonButton>
        </div>
      )}
      {type === "requested" && (
        <div className="flex items-center gap-2">
          <CommonButton variant="positive" className="pc:px-10" onClick={handleAcceptFriend}>
            수락
          </CommonButton>
          <CommonButton variant="negative" className="pc:px-10" onClick={handleDeleteFriend}>
            거절
          </CommonButton>
        </div>
      )}
      {type === "search" && (
        <div className="flex items-center gap-2">
          <CommonButton
            variant={isRequested ? "neutral-outline" : "positive"}
            className={`w-24 pc:px-10 ${isRequested ? "pointer-events-none opacity-50" : ""}`}
            onClick={handleRequestFriend}
          >
            {isRequested ? "요청중..." : "친구 요청"}
          </CommonButton>
        </div>
      )}
    </div>
  );
};

export default FriendItem;
