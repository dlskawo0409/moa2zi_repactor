import { useEffect, useState } from "react";
import { getChallenges } from "@/services/challenge";
import { convertToChallengeCardProps } from "@/utils/challengeMapper";
import { useJoinChallenge } from "@/hooks/useJoinChallenge";
import ChallengeList from "@/components/challenge/ChallengeList";
import NullMyChallenge from "@/components/challenge/NullMyChallenge";

const MyChallengeList = ({ keyword, tag }: { keyword: string; tag: string }) => {
  const { challenges, setChallenges, handleJoinChallenge } = useJoinChallenge();
  const [isLoading, setIsLoading] = useState<boolean>(true);

  useEffect(() => {
    const fetchData = async () => {
      const response = await getChallenges({
        type: "MINE",
        keyword: keyword || undefined,
        tag: tag || undefined,
        size: 60,
      });
      const mapped = response.map(convertToChallengeCardProps);
      setChallenges(mapped);
      setIsLoading(false);
    };
    fetchData();
  }, [keyword, tag, setChallenges]);

  return (
    // <div className="w-full px-4 ">
    //   <ChallengeList challenges={challenges} onJoinChallenge={handleJoinChallenge} />
    // </div>

    <div className="w-full px-4">
      {isLoading ? (
        <div className="w-full text-center py-10 text-gray-400">로딩 중...</div>
      ) : challenges.length > 0 ? (
        <ChallengeList challenges={challenges} onJoinChallenge={handleJoinChallenge} />
      ) : (
        <NullMyChallenge />
      )}
    </div>
  );
};

export default MyChallengeList;
