import { useEffect, useState } from "react";
import { getChallengeReviews, postChallengeReviewLike } from "@/services/challenge";
import { getProfileIcon } from "@/utils/getProfileIcon";
import { formatKoreanDate } from "@/utils/formatDate";
import ReviewForm from "@/components/challenge/ReviewForm";

const ChallengeDetail = ({
  challengeId,
  challengeTimeId,
  status,
}: {
  challengeId: number;
  challengeTimeId: number;
  status: string;
}) => {
  const [likedState, setLikedState] = useState<boolean[]>([]);
  const [reviews, setReviews] = useState<any[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  const fetchReviews = async () => {
    setLoading(true);
    try {
      const reviewData = await getChallengeReviews(challengeId, { size: 5 });
      setReviews(reviewData);
      setLikedState(new Array(reviewData.length).fill(false));
    } catch (error) {
      // console.error("리뷰 불러오기 실패:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchReviews();
  }, [challengeTimeId]);

  const toggleLike = async (index: number, challengeParticipantId: number) => {
    try {
      await postChallengeReviewLike(challengeParticipantId);

      setReviews((prev) => {
        const updated = [...prev];
        const review = updated[index];

        updated[index] = {
          ...review,
          isLikedByMe: !review.isLikedByMe,
          reviewLikeCount: review.reviewLikeCount + (review.isLikedByMe ? -1 : 1),
        };

        return updated;
      });
    } catch (error) {
      // console.error("좋아요 요청 실패:", error);
    }
  };

  return (
    <div className="bg-white px-4 py-4" onClick={(e) => e.stopPropagation()}>
      <div className="text-lg font-bold mb-2">성공 후기</div>
      {loading ? (
        <p>성공 후기 탐색중...</p>
      ) : reviews.length === 0 ? (
        <div>
          <p className="text-neutral-500">아직 성공 후기가 없습니다.</p>
        </div>
      ) : (
        reviews.map((review, i) => {
          const Icon = getProfileIcon(review.participantInfo.profileImage);

          return (
            <div key={i} className="items-center gap-3 py-2 border-b last:border-none">
              <div className="flex gap-3 items-center">
                <div className="w-10 h-10 rounded-full overflow-hidden bg-white border border-neutral-300">
                  {Icon && <Icon className="w-full h-full" />}
                </div>
                <div className="w-full">
                  <div className="flex items-center justify-between">
                    <span className="text-xm">{review.participantInfo.nickname}</span>
                    <span className="flex justify-end text-xs text-gray-500 whitespace-nowrap">
                      후기 작성일 : {formatKoreanDate(review.reviewedAt)}
                    </span>
                  </div>
                  <div className="flex justify-between items-start">
                    <span className="text-sm pc:pr-12 pc-4">{review.review}</span>
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        toggleLike(i, review.participantInfo.challengeParticipantId);
                      }}
                      className="ml-auto flex items-center gap-1 text-pink-500 text-sm"
                    >
                      {review.isLikedByMe ? "❤️" : "🤍"}{" "}
                      <span className="w-4">{review.reviewLikeCount}</span>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          );
        })
      )}
      {status === "SUCCESS" && (
        <ReviewForm challengeTimeId={challengeTimeId} onReviewSubmit={fetchReviews} />
      )}
    </div>
  );
};

export default ChallengeDetail;
