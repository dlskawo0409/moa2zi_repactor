import { useEffect, useState, useRef, useCallback } from "react";
import { useParams } from "react-router-dom";
import NavBar from "@components/common/NavBar";
import TransactionItem from "@components/calendar/TransactionItem";
import CommentItem from "@components/calendar/CommentItem";
import SendIcon from "@components/svgs/SendIcon";
import {
  getCalendarDate,
  getCalendarComments,
  patchEmotion,
  postCalendarComment,
} from "@/services/calendar";
import { useUserInfo } from "@/hooks/useUserInfo";
import { TransactionList, EMOTION } from "@/types/calendar";

const AccountBookCalendarDatePage = () => {
  const { memberId, dayId, transactionDate } = useParams();
  const year = transactionDate?.substring(0, 4);
  const month = transactionDate?.substring(4, 6);
  const day = transactionDate?.substring(6, 8);

  const { data } = useUserInfo();

  const [transactionList, setTransactionList] = useState<TransactionList[]>([]);
  const [openEmotionIndex, setOpenEmotionIndex] = useState<number | null>(null);

  const [comments, setComments] = useState<any[]>([]);
  const [total, setTotal] = useState<number>(0);
  const [next, setNext] = useState<number | null>(0);
  const [hasNext, setHasNext] = useState<boolean>(true);

  const observerRef = useRef<IntersectionObserver | null>(null);

  const fetchData = async () => {
    try {
      const response = await getCalendarDate({
        memberId: memberId,
        transactionDate,
      });
      setTransactionList(response.data.transactionList);
    } catch (error) {
      // console.log(error);
    }
  };

  const fetchComments = async () => {
    if (!hasNext || next === null) return;
    try {
      const response = await getCalendarComments({
        dayId,
        request: {
          parentId: null,
          next,
          size: 5,
        },
      });

      setComments((prev) => [...prev, ...response.data.commentList]);
      setNext(response.data.next);
      setHasNext(response.data.hasNext);
      setTotal(response.data.total);
    } catch (error) {
      // console.log("에러", error);
    }
  };

  const lastCommentRef = useCallback(
    (node: HTMLDivElement) => {
      if (observerRef.current) observerRef.current.disconnect();
      observerRef.current = new IntersectionObserver((entries) => {
        if (entries[0].isIntersecting && hasNext) {
          fetchComments();
        }
      });
      if (node) observerRef.current.observe(node);
    },
    [hasNext, next],
  );

  useEffect(() => {
    fetchData();
    fetchComments();
  }, []);

  const [replyTargetId, setReplyTargetId] = useState<number | null>(null);
  const [replyTarget, setReplyTarget] = useState<string | null>(null);
  const [replyTargetUser, setReplyTargetUser] = useState<string | null>(null);

  const handleEmotionSelect = async (emotion: EMOTION, transactionId: number) => {
    try {
      await patchEmotion({ emotion, transactionId });
      setOpenEmotionIndex(null);
      setTransactionList((prev) =>
        prev.map((item) =>
          item.transactionId === transactionId
            ? { ...item, emotion } // fetchData() 대신 emotion만 바꿔치기
            : item,
        ),
      );
    } catch (error) {
      // console.log(error);
    }
  };

  // 댓글 텍스트 상태
  const [commentText, setCommentText] = useState<string>("");

  const handleCommentSubmit = async () => {
    if (commentText.trim() === "") return;

    // console.log("아이디", replyTargetId);
    const request = {
      dayId,
      request: {
        parentId: replyTargetId ? replyTargetId : null,
        content: commentText,
      },
    };

    try {
      await postCalendarComment(request);
      setCommentText("");
      setReplyTargetId(null);
      setReplyTarget(null);
      setReplyTargetUser(null);

      setComments([]);
      setNext(0);
      setHasNext(true);
      setResetComments(true);
    } catch (error) {
      // console.log("댓글 작성 실패:", error);
    }
  };

  function openItem(transactionId: number) {
    setOpenEmotionIndex(transactionId);
  }

  // function closeItem() {
  //   setOpenEmotionIndex(null);
  // }

  // 댓글 리셋 트리거
  const [resetComments, setResetComments] = useState<boolean>(false);

  useEffect(() => {
    if (resetComments) {
      fetchComments();
      setResetComments(false);
    }
  }, [resetComments]);

  return (
    <div>
      {memberId == data?.memberId && <NavBar />}
      <div className={`flex flex-col w-full gap-6 ${replyTarget && "pb-36"} pb-20 pt-5`}>
        <div className="flex w-full text-2xl justify-center items-center px-6">
          {year}년 {month}월 {day}일
        </div>
        <>
          <div className="px-5 pc:px-10">
            {transactionList
              .filter((item) => item.transactionType === "SPEND")
              .map((item) => (
                <TransactionItem
                  key={item.transactionId}
                  index={item.transactionId}
                  item={item}
                  isOpen={openEmotionIndex}
                  onToggleOpen={() => {
                    // console.log(item.transactionId);
                    setOpenEmotionIndex((prev) =>
                      prev === item.transactionId ? null : item.transactionId,
                    );
                  }}
                  // onClose={closeItem}
                  onEmotionSelect={handleEmotionSelect}
                />
              ))}
          </div>
        </>
        <div className="flex flex-col w-full px-5 pc:px-10">
          <div className="border-b border-neutral-300 py-3">댓글 ({total})</div>
          {comments.map((comment, index) => (
            <div key={index} ref={index === comments.length - 1 ? lastCommentRef : null}>
              <CommentItem
                comment={comment}
                setReplyTargetUser={setReplyTargetUser}
                setReplyTarget={setReplyTarget}
                setReplyTargetId={setReplyTargetId}
              />
            </div>
          ))}
        </div>
      </div>

      <div className="fixed w-full pc:w-[598px] bottom-16 flex flex-col items-center bg-white border-t border-neutral-200 transition-all duration-300 ease-in-out">
        {replyTarget && (
          <div className="flex justify-between w-full px-4 py-2 text-sm border-b border-neutral-300">
            <div className="flex flex-col gap-1">
              <div className="font-bold">{replyTargetUser}</div>
              <div>{replyTarget}</div>
            </div>
            <div
              className="flex items-center text-negative-500 cursor-pointer"
              onClick={() => {
                setReplyTarget(null);
                setReplyTargetUser(null);
              }}
            >
              취소
            </div>
          </div>
        )}
        <div className="flex flex-col items-center w-full">
          <div className="flex items-center w-full gap-2 p-3">
            <textarea
              className="flex-1 border border-neutral-300 rounded-md px-5 pr-12 scrollbar-hide py-2 focus:outline-none text-sm resize-none min-h-[2.5rem] max-h-[6rem]"
              placeholder="댓글 입력"
              rows={1}
              value={commentText}
              onChange={(e) => setCommentText(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter" && !e.shiftKey) {
                  e.preventDefault();
                  handleCommentSubmit();
                }
              }}
            />
            <div className="absolute right-6 size-6 cursor-pointer" onClick={handleCommentSubmit}>
              <SendIcon className="stroke-neutral-500 fill-primary-500" />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AccountBookCalendarDatePage;
