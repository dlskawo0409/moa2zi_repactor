import { useEffect, useState } from "react";
import { Toaster } from "sonner";
import {
  Carousel,
  CarouselContent,
  CarouselItem,
  CarouselNext,
  CarouselPrevious,
  type CarouselApi,
} from "@/components/ui/carousel";
import PopularChallenge from "@/components/challenge/PopularChallenge";
import ChallengeList from "@/components/challenge/ChallengeList";
import { getChallenges } from "@/services/challenge";
import { convertToChallengeCardProps } from "@/utils/challengeMapper";
import { useJoinChallenge } from "@/hooks/useJoinChallenge";

const AllChallengeList = ({ keyword, tag }: { keyword: string; tag: string }) => {
  const [carouselApi, setCarouselApi] = useState<CarouselApi | null>(null);
  const [openDetailIdx, setOpenDetailIdx] = useState<number | null>(null);
  const [popularChallenges, setPopularChallenges] = useState<any[]>([]);
  const { challenges, setChallenges, handleJoinChallenge } = useJoinChallenge();

  useEffect(() => {
    const fetchData = async () => {
      const response = await getChallenges({
        type: "SEARCH",
        keyword: keyword || undefined,
        tag: tag || undefined,
        size: 0,
      });
      const mapped = response.map(convertToChallengeCardProps);
      setChallenges(mapped);
    };

    fetchData();
  }, [keyword, tag, setChallenges]);

  // 인기 챌린지 조회
  useEffect(() => {
    const fetchPopularChallenges = async () => {
      const data = await getChallenges({ type: "POPULAR", size: 3 });
      const mapped = data.map(convertToChallengeCardProps);
      setPopularChallenges(mapped);
    };
    fetchPopularChallenges();
  }, []);

  useEffect(() => {
    if (!carouselApi) return;

    let interval: NodeJS.Timeout;

    const startAutoSlide = () => {
      clearInterval(interval);
      interval = setInterval(() => {
        carouselApi.scrollNext();
      }, 5000);
    };

    // 슬라이드 선택/이동 시마다 타이머 리셋
    carouselApi.on("select", startAutoSlide);

    // 초기에 한번 시작
    startAutoSlide();

    return () => {
      clearInterval(interval);
      carouselApi.off("select", startAutoSlide);
    };
  }, [carouselApi]);

  return (
    <>
      {/* <Toaster
        position="top-center"
        toastOptions={{
          className: "flex justify-between left-center w-full  min-w-[92.5%] custom-toast-negative",
        }}
      /> */}
      <div className="w-full px-4 py-3 ">
        <div className="relative w-full mb-4 shadow-md rounded-xl">
          <Carousel setApi={setCarouselApi}>
            {popularChallenges.length === 0 ? (
              <div className="text-center py-8">인기 챌린지를 불러오는 중입니다...</div>
            ) : (
              <CarouselContent>
                {popularChallenges.map((challenge, idx) => (
                  <CarouselItem key={idx} className="basis-full">
                    <PopularChallenge
                      challenge={challenge}
                      idx={idx}
                      openDetailIdx={openDetailIdx}
                      setOpenDetailIdx={setOpenDetailIdx}
                      onJoinChallenge={handleJoinChallenge}
                    />
                  </CarouselItem>
                ))}
              </CarouselContent>
            )}
            <CarouselPrevious className="absolute top-1/2 left-2 -translate-y-1/2 z-20" />
            <CarouselNext className="absolute top-1/2 right-2 -translate-y-1/2 z-20" />
          </Carousel>
        </div>
        <ChallengeList challenges={challenges} onJoinChallenge={handleJoinChallenge} />
      </div>
    </>
  );
};

export default AllChallengeList;
