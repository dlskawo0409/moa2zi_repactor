import { useState } from "react";
import { toast } from "sonner";
import { postChallenge } from "@/services/challenge";
import { ChallengeCardProps } from "@/utils/challengeMapper";

export const useJoinChallenge = () => {
  const [challenges, setChallenges] = useState<ChallengeCardProps[]>([]);

  const handleJoinChallenge = async (challengeTimeId: number) => {
    try {
      // console.log("challengeTimeId", challengeTimeId);
      await postChallenge(challengeTimeId);
      setChallenges((prev) =>
        prev.map((ch) =>
          ch.challengeTimeId === challengeTimeId ? { ...ch, isParticipating: true } : ch,
        ),
      );
      // toast("챌린지에 참여하였습니다 🎉");
      return true;
    } catch (error: any) {
      toast("이미 참여한 챌린지예요!");
      return false;
    }
  };

  return {
    challenges,
    setChallenges,
    handleJoinChallenge,
  };
};
