import { useState } from "react";
import ChallengeSearchBar from "@/components/challenge/ChallengeSearchBar";
import MyChallengeList from "@/components/challenge/MyChallengeList";

const MyChallenge = () => {
  const [keyword, setKeyword] = useState<string>("");
  const [tag, setTag] = useState<string>("");

  const handleSearch = ({ keyword, tag }: { keyword: string; tag: string }) => {
    setKeyword(keyword);
    setTag(tag);
  };

  return (
    <div>
      <ChallengeSearchBar onSearch={handleSearch} />
      <MyChallengeList keyword={keyword} tag={tag} />
    </div>
  );
};

export default MyChallenge;
