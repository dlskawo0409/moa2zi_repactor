import { useEffect, useState } from "react";
import { useNavigate, useParams, useLocation } from "react-router-dom";
import YesMouseIcon from "@components/svgs/lounge/YesMouseIcon";
import NoMouseIcon from "@components/svgs/lounge/NoMouseIcon";
import CheckIcon from "@components/svgs/CheckIcon";
import XIcon from "@components/svgs/XIcon";
import CommonButton from "@components/common/CommonButton";
import GameIcon from "@components/svgs/GameIcon";
import { getGameResult } from "@/services/lounge";
import { useUserInfo } from "@/hooks/useUserInfo";
import { QuizResult } from "@/types/lounge";

const LoungeGameResultPage = () => {
  const { data } = useUserInfo();

  const { loungeId, gameId } = useParams<{ loungeId: string; gameId: string }>();
  const location = useLocation();
  const navigate = useNavigate();

  const [rightAnswerCount, setRightAnswerCount] = useState<number>();
  const [totalQuizCount, setTotalQuizCount] = useState<number>();
  const [resultList, setResultList] = useState<QuizResult[]>([]);

  const fetchData = async () => {
    try {
      const response = await getGameResult({
        memberId: data?.memberId,
        gameId: gameId,
      });

      // console.log(response.data);
      setRightAnswerCount(response.data.rightAnswerCount);
      setTotalQuizCount(response.data.totalQuizCount);
      setResultList(response.data.quizWithResultResponseList);
    } catch (error) {
      // console.log(error);
    }
  };

  useEffect(() => {
    if (data?.memberId && gameId) {
      fetchData();
    }
  }, [data, gameId]);

  return (
    <div className="flex flex-col justify-center items-center p-8 pc:p-10">
      <div className="flex flex-col justify-center items-center gap-2">
        <div className="text-xl font-bold">퀴즈 풀이 결과</div>
        <div className="text-lg  mb-5">
          {totalQuizCount}문제 중 총 {rightAnswerCount}문제 맞추셨습니다.
        </div>
      </div>
      <div className="flex w-full justify-end text-xs pc:text-sm pr-1 pc:pr-4">내 정답</div>
      <div className="flex flex-col w-full  mb-5">
        {resultList.map((problem, index) => (
          <div
            key={index}
            className={`flex items-center justify-between w-full pc:px-3 py-3 text-sm pc:text-md ${
              index !== resultList.length - 1 ? "border-b border-neutral-200" : ""
            }`}
          >
            <div className="flex relative size-7 bg-neutral-200 rounded-full items-center justify-center">
              {index + 1}
              <div className="absolute -right-2 -top-2">
                {problem.isCorrect ? (
                  <CheckIcon className="size-5 stroke-positive-500 " />
                ) : (
                  <XIcon className="size-6 stroke-negative-500" />
                )}
              </div>
            </div>
            <div className="flex flex-1 justify-start text-start text-sm pc:text-md px-5 whitespace-pre-wrap break-keep">
              {problem.content}
            </div>
            {problem.submittedAnswer == "YES" ? (
              <YesMouseIcon className="size-10 pc:size-12" />
            ) : (
              <NoMouseIcon className="size-10 pc:size-12" />
            )}
          </div>
        ))}
      </div>
      <CommonButton
        variant="primary"
        className="w-full"
        onClick={() =>
          navigate(
            location.pathname.startsWith("/profile")
              ? `/profile/room/game/rank/${loungeId}/${gameId}`
              : `/lounge/room/game/rank/${loungeId}/${gameId}`,
          )
        }
      >
        <GameIcon />내 등수 보기
      </CommonButton>
    </div>
  );
};

export default LoungeGameResultPage;
