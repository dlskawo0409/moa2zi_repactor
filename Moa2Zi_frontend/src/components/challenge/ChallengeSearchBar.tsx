import { useState, ChangeEvent, KeyboardEvent } from "react";
import { Search } from "lucide-react";

const ChallengeSearchBar = ({
  onSearch,
}: {
  onSearch: (query: { keyword: string; tag: string }) => void;
}) => {
  const [query, setQuery] = useState<string>("");
  const [showError, setShowError] = useState<boolean>(false);

  const handleSearch = () => {
    if (query.length > 50) {
      setShowError(true);
      return;
    }

    const rawWords = query.trim().split(/\s+/);
    const firstTag = rawWords.find((word) => word.startsWith("#"));
    const tag = firstTag ? firstTag.slice(1) : "";

    const keywords = rawWords.filter((word) => word !== firstTag);
    const keyword = keywords.join(" ");

    onSearch({ keyword, tag });
  };

  const handleInputChange = (e: ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    const hashCount = (value.match(/#/g) || []).length;

    if (value.length <= 50) {
      if (hashCount <= 1) {
        setQuery(value);
        setShowError(false);
      } else {
        // 두 번째 # 입력 시 제한
        setShowError(true);
      }
    } else {
      setShowError(true);
    }
  };

  const handleKeyDown = (e: KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      handleSearch();
    }
  };

  return (
    <div className="px-4 py-1">
      <div
        className={`w-full h-10 px-3 flex items-center rounded-lg border-2 bg-neutral-50 shadow-sm transition-all duration-200 
      ${showError ? "border-negative-500" : "border-gray-300 focus-within:border-primary-500"}`}
      >
        <input
          type="text"
          value={query}
          onChange={handleInputChange}
          onKeyDown={handleKeyDown}
          placeholder={
            showError ? "태그는 하나만 입력할 수 있어요" : "챌린지를 검색하세요. 예: 다이어트 #운동"
          }
          className={`flex-grow text-sm bg-neutral-50 placeholder-gray-400 focus:outline-none 
          ${showError ? "text-negative-500 placeholder-negative-500" : "text-gray-700"}`}
          maxLength={51}
        />
        <button onClick={handleSearch} className="ml-2 text-gray-500 hover:text-gray-700">
          <Search size={16} />
        </button>
      </div>
    </div>
  );
};

export default ChallengeSearchBar;
