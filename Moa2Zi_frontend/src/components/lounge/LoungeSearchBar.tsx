import { ChevronDown, Search } from "lucide-react";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuRadioGroup,
  DropdownMenuRadioItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Input } from "@components/ui/input";

type FilterMode = "TITLE" | "NICKNAME";

interface LoungeSearchBarProps {
  filterMode: FilterMode;
  setFilterMode: (value: FilterMode) => void;
  keyword: string;
  setKeyword: (value: string) => void;
  handleSearch: () => void;
}

const LoungeSearchBar = ({
  filterMode,
  setFilterMode,
  keyword,
  setKeyword,
  handleSearch,
}: LoungeSearchBarProps) => {
  const filterModeDisplay = {
    TITLE: "라운쥐 이름",
    NICKNAME: "라운쥐 멤버",
  };

  return (
    <div className="flex justify-between gap-3">
      <div className="flex w-28 pc:w-32 gap-1 border-2 bg-neutral-50 border-neutral-200 rounded-md items-center px-3 pc:px-4 text-sm">
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <div className="flex text-xs pc:text-sm w-full gap-1 cursor-pointer justify-between items-center">
              <div>{filterModeDisplay[filterMode]}</div>
              <div className="flex w-4 h-full justify-center items-center">
                <ChevronDown />
              </div>
            </div>
          </DropdownMenuTrigger>
          <DropdownMenuContent>
            <DropdownMenuRadioGroup
              value={filterMode}
              onValueChange={(value) => setFilterMode(value as FilterMode)}
            >
              <DropdownMenuRadioItem value="TITLE">라운쥐 이름</DropdownMenuRadioItem>
              <DropdownMenuRadioItem value="NICKNAME">라운쥐 멤버</DropdownMenuRadioItem>
            </DropdownMenuRadioGroup>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>

      <div className="relative flex-1">
        <Input
          placeholder="라운쥐를 검색하세요"
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          className=" border-2 border-neutral-200 bg-neutral-50 text-sm py-2 focus:border-primary-500 focus-visible:ring-primary-500"
          onKeyDown={(e) => {
            if (e.key === "Enter") handleSearch();
          }}
        />
        <button className="absolute right-3 top-2 text-neutral-500" onClick={handleSearch}>
          <Search className="size-5" />
        </button>
      </div>
    </div>
  );
};

export default LoungeSearchBar;
