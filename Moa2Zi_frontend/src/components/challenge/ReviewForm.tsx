import { useState, ChangeEvent, KeyboardEvent, FormEvent, useRef } from "react";
import { postChallengeReview } from "@/services/challenge";
import SendIcon from "@components/svgs/SendIcon";

const ReviewForm = ({
  challengeTimeId,
  onReviewSubmit,
}: {
  challengeTimeId: number;
  onReviewSubmit?: () => void;
}) => {
  const [reviewText, setReviewText] = useState<string>("");
  const reviewInputRef = useRef<HTMLTextAreaElement | null>(null);

  const handleReviewChange = (event: ChangeEvent<HTMLTextAreaElement>) => {
    setReviewText(event.target.value);

    if (reviewInputRef.current) {
      reviewInputRef.current.style.height = "auto";
      reviewInputRef.current.style.height = `${reviewInputRef.current.scrollHeight}px`;
    }
  };

  const handleKeyDown = (e: KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSubmit(e);
    }
  };

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();

    const trimmed = reviewText.trim();

    if (!trimmed) return;

    try {
      await postChallengeReview(challengeTimeId, trimmed);
      alert("후기가 성공적으로 작성되었습니다.");
      setReviewText("");

      if (reviewInputRef.current) {
        reviewInputRef.current.style.height = "auto";
      }

      if (onReviewSubmit) {
        onReviewSubmit();
      }
    } catch (error) {
      // console.error("후기 작성 실패:", error);
      alert("이미 후기를 등록하였습니다.");
    }
  };

  return (
    <div className="bg-white py-2 rounded-xl">
      <form
        onSubmit={handleSubmit}
        className="flex items-center border text-sm border-gray-300 rounded-xl px-4 py-2"
      >
        <textarea
          className="flex-1 w-full focus:outline-none scrollbar-hide resize-none text-sm placeholder-gray-500 min-h-[1rem] max-h-[6rem]"
          ref={reviewInputRef}
          rows={1}
          minLength={5}
          maxLength={200}
          value={reviewText}
          onChange={handleReviewChange}
          onKeyDown={handleKeyDown}
          placeholder="챌린지를 완료하셨군요! 후기를 남겨주세요."
        />
        <button type="submit" className="ml-2 text-orange-400 hover:text-orange-500 transition">
          <SendIcon className="stroke-neutral-500 fill-primary-500" />
        </button>
      </form>
    </div>
  );
};

export default ReviewForm;
