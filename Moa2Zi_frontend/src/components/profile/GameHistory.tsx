import { useEffect, useState } from "react";

import GameHistoryItem from "@/components/profile/GameHistoryItem";
import GameHistoryItemSkeleton from "@/components/profile/GameHistoryItemSkeleton";
import { getGamesHistories } from "@/services/game";
import { GameHistoryData } from "@/types/game";

const GameHistory = () => {
  const [histories, setHistories] = useState<GameHistoryData[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    const fetchHistories = async () => {
      try {
        const data = await getGamesHistories();
        setHistories(data);
      } catch (error) {
        // console.error("게임 기록을 불러오는데 실패했어요:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchHistories();
  }, []);

  const formatKoreanDate = (dateString: string) => {
    const date = new Date(dateString);
    return `${date.getFullYear()}년 ${date.getMonth() + 1}월 ${date.getDate()}일`;
  };

  return (
    <div className="flex flex-col gap-4 p-5">
      {loading ? (
        Array.from({ length: 3 }).map((_, index) => <GameHistoryItemSkeleton key={index} />)
      ) : histories.length === 0 ? (
        <div className="text-center text-gray-500 mt-10">게임 진행 기록이 없어요</div>
      ) : (
        histories.map((history) => (
          <GameHistoryItem
            key={history.gameId}
            loungeId={history.loungeId}
            gameId={history.gameId}
            rank={history.ranking}
            name={history.loungeName}
            time={formatKoreanDate(history.endTime)}
            participant={history.totalMembers}
          />
        ))
      )}
    </div>
  );
};

export default GameHistory;
