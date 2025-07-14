import { Dispatch, SetStateAction } from "react";
import { formatKebab } from "@/utils/formatDate";
import { getProfileIcon } from "@/utils/getProfileIcon";

interface ReplyItemProps {
  reply: any;
  commentId: number;
  setReplyTarget: Dispatch<SetStateAction<string | null>>;
  setReplyTargetUser: Dispatch<SetStateAction<string | null>>;
  setReplyTargetId: Dispatch<SetStateAction<number | null>>;
}

const ReplyItem = ({
  reply,
  commentId,
  setReplyTarget,
  setReplyTargetUser,
  setReplyTargetId,
}: ReplyItemProps) => {
  const onClickReply = () => {
    setReplyTargetUser(reply.member.nickname);
    setReplyTarget(reply.content);
    setReplyTargetId(commentId);
  };

  const Icon = getProfileIcon(reply.member.profileImage);

  return (
    <div className="py-3 ml-10 px-3 bg-neutral-50 rounded-md">
      <div className="flex w-full justify-between">
        <div className="flex items-center gap-4">
          {Icon && (
            <Icon className="size-9 pc:size-10 border border-neutral-200 rounded-full bg-white" />
          )}

          <div className="font-semibold text-sm">{reply.member.nickname}</div>
        </div>
        <div className="flex items-center text-xs text-neutral-600">
          {formatKebab(reply.updatedAt)}
        </div>
      </div>
      <div className="flex text-sm ml-14">{reply.content}</div>
      <div className="flex gap-3 ml-14 text-xs mt-2 text-neutral-600 cursor-pointer">
        <div onClick={onClickReply}>답글</div>
        <div>삭제</div>
      </div>
    </div>
  );
};

export default ReplyItem;
