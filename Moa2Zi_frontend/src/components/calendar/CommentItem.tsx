import { useState, Dispatch, SetStateAction } from "react";
import { useParams } from "react-router-dom";
import { ChevronDown } from "lucide-react";
import ReplyItem from "@/components/calendar/ReplyItem";
import { getCalendarComments } from "@/services/calendar";
import { formatKebab } from "@/utils/formatDate";
import { getProfileIcon } from "@/utils/getProfileIcon";

interface CommentItemProps {
  comment: any;
  setReplyTarget: Dispatch<SetStateAction<string | null>>;
  setReplyTargetUser: Dispatch<SetStateAction<string | null>>;
  setReplyTargetId: Dispatch<SetStateAction<number | null>>;
}

const CommentItem = ({
  comment,
  setReplyTarget,
  setReplyTargetUser,
  setReplyTargetId,
}: CommentItemProps) => {
  const { dayId } = useParams();
  const [openReply, setOpenReply] = useState<boolean>(false);
  const [replies, setReplies] = useState<any[]>([]);

  const Icon = getProfileIcon(comment.member.profileImage);

  const onClickReply = () => {
    setReplyTargetUser(comment.member.nickname);
    setReplyTarget(comment.content);
    setReplyTargetId(comment.commentId);
  };

  const handleToggleReplies = async () => {
    if (!openReply) {
      try {
        const response = await getCalendarComments({
          dayId: dayId,
          request: {
            parentId: comment.commentId,
            next: 0,
            size: 20,
          },
        });
        setReplies(response.data.commentList);
        // console.log(response.data);
      } catch (error) {
        // console.log("대댓글 불러오기 실패:", error);
      }
    }
    setOpenReply((prev) => !prev);
  };

  return (
    <>
      <div className="py-2">
        <div className="flex w-full justify-between">
          <div className="flex items-center gap-4">
            {Icon && (
              <Icon className="size-9 pc:size-10 border border-neutral-200 rounded-full bg-white" />
            )}
            <div className="font-semibold text-sm">{comment.member.nickname}</div>
          </div>
          <div className="flex items-center text-xs text-neutral-600">
            {formatKebab(comment.updatedAt)}
          </div>
        </div>
        <div className="flex text-sm ml-14">{comment.content}</div>
        <div className="flex gap-3 ml-14 text-xs mt-2 text-neutral-600 cursor-pointer">
          <div onClick={onClickReply}>답글</div>
          <div>삭제</div>
        </div>

        {comment.childCommentCount > 0 && (
          <div
            className="flex mt-2 items-center text-xs gap-2 text-primary-500 cursor-pointer"
            onClick={handleToggleReplies}
          >
            <ChevronDown className="flex ml-14 size-4" />
            <div>댓글 {comment.childCommentCount}개</div>
          </div>
        )}
      </div>

      {openReply &&
        replies.map((reply) => (
          <ReplyItem
            key={reply.commentId}
            reply={reply}
            setReplyTarget={setReplyTarget}
            setReplyTargetUser={setReplyTargetUser}
            setReplyTargetId={setReplyTargetId}
            commentId={comment.commentId}
          />
        ))}
    </>
  );
};

export default CommentItem;
