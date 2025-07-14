import { useState } from "react";
import ChallengeSearchBar from "@/components/challenge/ChallengeSearchBar";
import AllChallengeList from "@/components/challenge/AllChallengeList";

const AllChallenge = () => {
  const [keyword, setKeyword] = useState<string>("");
  const [tag, setTag] = useState<string>("");

  const handleSearch = ({ keyword, tag }: { keyword: string; tag: string }) => {
    setKeyword(keyword);
    setTag(tag);
  };

  return (
    <div>
      <ChallengeSearchBar onSearch={handleSearch} />
      <AllChallengeList keyword={keyword} tag={tag} />
    </div>
  );
};

export default AllChallenge;
