import { useEffect, useState } from "react";
import { useNavigate, useParams, useLocation } from "react-router-dom";
import GameResultPodium from "@/components/lounge/GameResultPodium";
import CommonButton from "@/components/common/CommonButton";
import GameIcon from "@/components/svgs/GameIcon";
import GoldMedalIcon from "@/components/svgs/GoldMedalIcon";
import SilverMedalIcon from "@components/svgs/SilverMedalIcon";
import BronzeMedalIcon from "@/components/svgs/BronzeMedalIcon";
import { getGameRanking } from "@/services/lounge";
import { useUserInfo } from "@/hooks/useUserInfo";
import { QuizRank } from "@/types/lounge";

const LoungeGameRankPage = () => {
  const { data } = useUserInfo();
  const { loungeId, gameId } = useParams<{ loungeId: string; gameId: string }>();
  const location = useLocation();
  const navigate = useNavigate();

  const [myRanking, setMyRanking] = useState<number>();
  const [totalMemberCount, setTotalMemberCount] = useState<number>();
  const [totalQuizCount, setTotalQuizCount] = useState<number>();
  const [rankList, setRankList] = useState<QuizRank[]>([]);

  const fetchData = async () => {
    try {
      const response = await getGameRanking({
        memberId: data?.memberId,
        gameId: gameId,
      });
      // console.log(response.data);
      setMyRanking(response.data.myRanking);
      setTotalMemberCount(response.data.totalMemberCount);
      setTotalQuizCount(response.data.totalQuizCount);
      setRankList(response.data.quizWithRankingResponseList);
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
    <div className="flex flex-col justify-center items-center py-8 px-5 pc:p-10 gap-10">
      <div className="flex flex-col justify-center items-center gap-2">
        <div className="text-xl font-bold">순위의 전당</div>
        <div className="text-lg">
          {totalMemberCount}명 중 {myRanking}위를 차지하셨습니다!
        </div>
      </div>
      <GameResultPodium rankList={rankList} />
      <div className="flex flex-col w-full px-4 pc:px-10 items-center gap-5 text-sm">
        {rankList.map((rank, index) => (
          <div key={rank.memberId} className="flex w-full h-10 justify-between items-center">
            <div className="flex items-center justify-center gap-2">
              {index === 0 ? (
                <GoldMedalIcon className="w-7" />
              ) : index === 1 ? (
                <SilverMedalIcon className="w-7" />
              ) : index === 2 ? (
                <BronzeMedalIcon className="w-7" />
              ) : (
                <div className="flex w-5 h-5 mx-1 text-center items-center justify-center text-sm bg-neutral-200 rounded-full">
                  {index + 1}
                </div>
              )}
              <div>{rank.nickname}</div>
            </div>
            <div>
              {totalQuizCount}문제 중 {rank.correctCount}문제를 맞추셨어요 :)
            </div>
          </div>
        ))}
      </div>
      <CommonButton
        variant="primary"
        className="w-full mx-5"
        onClick={() =>
          navigate(
            location.pathname.startsWith("/profile") ? "/profile" : `/lounge/room/${loungeId}`,
          )
        }
      >
        <GameIcon />
        {location.pathname.startsWith("/profile") ? "프로필" : "라운쥐"}로 돌아가기
      </CommonButton>
    </div>
  );
};

export default LoungeGameRankPage;
