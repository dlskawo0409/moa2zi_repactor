import { useState } from "react";
import ChallengeSearchBar from "@/components/challenge/ChallengeSearchBar";
import RecommendChallengeList from "@/components/challenge/RecommendChallengeList";

const RecommendChallenge = () => {
  const [keyword, setKeyword] = useState<string>("");
  const [tag, setTag] = useState<string>("");

  const handleSearch = ({ keyword, tag }: { keyword: string; tag: string }) => {
    setKeyword(keyword);
    setTag(tag);
  };

  return (
    <div>
      <ChallengeSearchBar onSearch={handleSearch} />
      <RecommendChallengeList keyword={keyword} tag={tag} />
    </div>
  );
};

export default RecommendChallenge;
