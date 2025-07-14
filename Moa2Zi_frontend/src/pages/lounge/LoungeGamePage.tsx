import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Progress } from "@/components/ui/progress";
import GameFinishedModal from "@components/lounge/GameFinishedModal";
import QuizBox from "@/components/lounge/QuizBox";
import { getQuiz, postQuizAnswer } from "@/services/lounge";
import { Quiz, LoungeWithGame, PostQuizAnswerRequest, QuizAnswer } from "@/types/lounge";
import { Skeleton } from "@components/ui/skeleton";

const LoungeGamePage = () => {
  const navigate = useNavigate();

  const { loungeId, gameId, quizId } = useParams<{
    loungeId: string;
    gameId: string;
    quizId: string;
  }>();
  const [isFinished, setIsFinished] = useState<boolean>(false);
  const [totalQuizSize, setTotalQuizSize] = useState<number>(0);
  const [nextQuizId, setNextQuizId] = useState<number>(0);
  const [nowCount, setNowCount] = useState<number>(0);
  const [loungeWithGame, setLoungeWithGame] = useState<LoungeWithGame>({
    createdAt: "",
    duration: 0,
    gameEndTime: "",
    id: 0,
    loungeEndTime: "",
    title: "",
  });
  const [quiz, setQuiz] = useState<Quiz>({
    id: 0,
    gameId: 0,
    context: "",
    answer: "YES",
  });

  const [remainingTime, setRemainingTime] = useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(false);

  const progress = isFinished ? 100 : ((nowCount - 1) / (totalQuizSize - 1)) * 100;

  const fetchData = async () => {
    try {
      setIsLoading(true);
      const response = await getQuiz(quizId);
      // console.log(response.data);
      setNextQuizId(response.data.nextQuizId);
      setTotalQuizSize(response.data.totalQuizSize);
      setNowCount(response.data.nowCount);
      setQuiz(response.data.quiz);
      setLoungeWithGame(response.data.loungeWithGame);
      if (response.data.nowCount === response.data.totalQuizSize) {
        setIsFinished(true);
      }
    } catch (error) {
      // console.log(error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    // console.log(quizId);
    if (quizId === "null") return;
    fetchData();
  }, [quizId]);

  useEffect(() => {
    if (!loungeWithGame?.gameEndTime) return;

    const interval = setInterval(() => {
      const now = new Date();
      const end = new Date(loungeWithGame.gameEndTime);
      const diff = end.getTime() - now.getTime();

      if (diff <= 0) {
        setRemainingTime("0시간 0분 0초");
        clearInterval(interval);
      } else {
        const totalSeconds = Math.floor(diff / 1000);
        const hours = Math.floor(totalSeconds / 3600);
        const minutes = Math.floor((totalSeconds % 3600) / 60);
        const seconds = totalSeconds % 60;
        setRemainingTime(`${hours}시간 ${minutes}분 ${seconds}초`);
      }
    }, 1000);

    return () => clearInterval(interval);
  }, [loungeWithGame]);

  const gameEndTime = new Date(loungeWithGame.gameEndTime);
  const targetEndDate = new Date(gameEndTime);
  targetEndDate.setDate(gameEndTime.getDate() - 1);
  const targetStartDate = new Date(gameEndTime);
  targetStartDate.setDate(gameEndTime.getDate() - loungeWithGame.duration - 1);

  const handleSubmitAnswer = async (submission: QuizAnswer) => {
    try {
      // console.log(quizId);
      const requestData: PostQuizAnswerRequest = {
        quizId: quizId,
        submission: submission,
      };
      const response = await postQuizAnswer(requestData);
      // console.log(response.data);
      if (nextQuizId) {
        navigate(`/lounge/room/game/quiz/${loungeId}/${gameId}/${nextQuizId}`);
      } else {
        navigate(`/lounge/room/game/quiz/${loungeId}/${gameId}/fin`);
      }
    } catch (error) {
      // console.log(error);
    }
  };

  useEffect(() => {
    const handlePopState = () => {
      navigate(`/lounge/room/${loungeId}`, { replace: true });
    };

    window.addEventListener("popstate", handlePopState);

    return () => {
      window.removeEventListener("popstate", handlePopState);
    };
  }, []);

  return (
    <div className="flex flex-col justify-center items-center px-5 p-10">
      <div className="w-full">
        <Progress
          value={progress}
          className="h-6 bg-white border-2 border-primary-500"
          indicatorColorClass="bg-primary-500"
        />
        <div className="flex justify-between text-sm pt-2">
          <div>0%</div>
          <div>100%</div>
        </div>
      </div>
      {quizId === "fin" ? (
        <div className="text-xl font-semibold text-primary-600 text-center mt-10">
          <GameFinishedModal />
        </div>
      ) : (
        <>
          <QuizBox
            nowCount={nowCount}
            context={quiz.context}
            targetStartDate={targetStartDate}
            targetEndDate={targetEndDate}
            onSubmit={handleSubmitAnswer}
            isLoading={isLoading}
          />
          {!remainingTime ? (
            <Skeleton className="w-40 h-5" />
          ) : (
            <div className="w-full flex justify-center text-sm text-primary-600">
              게임 종료까지 남은 시간: <span className="ml-1 font-semibold">{remainingTime}</span>
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default LoungeGamePage;
