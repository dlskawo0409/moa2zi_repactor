import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  Carousel,
  CarouselContent,
  CarouselItem,
  CarouselNext,
  CarouselPrevious,
} from "@/components/ui/carousel";
import LoungeChatGameListItem from "@/components/lounge/LoungeChatGameListItem";
import { getGameList } from "@/services/lounge";
import { GameInfo } from "@/types/lounge";

const LoungeChatGameList = () => {
  const { loungeId } = useParams<{ loungeId: string }>();
  const navigate = useNavigate();

  const [gameList, setGameList] = useState<GameInfo[]>([]);

  const onClickGame = (gameStatus: string, gameId: number, quizId: number) => {
    if (gameStatus == "RUNNING") {
      if (quizId) {
        navigate(`/lounge/room/game/quiz/${loungeId}/${gameId}/${quizId}`);
      } else {
        navigate(`/lounge/room/game/quiz/${loungeId}/${gameId}/fin`);
      }
    } else {
      navigate(`/lounge/room/game/quiz/${loungeId}/${gameId}/fin`);
    }
  };

  const fetchData = async () => {
    try {
      const response = await getGameList(loungeId);
      // console.log(response.data);
      setGameList(response.data);
    } catch (error) {
      // console.log(error);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  if (gameList.length === 0) {
    return <div className="text-center py-8 text-gray-500">진행된 게임이 없습니다.</div>;
  }

  return (
    <div className="flex justify-center w-full transition-all duration-300 ease-in-out">
      <Carousel className="w-full">
        <CarouselContent>
          {gameList.map((game, index) => {
            if (index % 2 === 0) {
              return (
                <CarouselItem key={game.gameId}>
                  <div className="flex">
                    <LoungeChatGameListItem game={game} onClick={onClickGame} />
                    {gameList[index + 1] && (
                      <LoungeChatGameListItem game={gameList[index + 1]} onClick={onClickGame} />
                    )}
                  </div>
                </CarouselItem>
              );
            }
            return null;
          })}
        </CarouselContent>
        <CarouselPrevious className="ml-14" />
        <CarouselNext className="mr-14" />
      </Carousel>
    </div>
  );
};

export default LoungeChatGameList;
