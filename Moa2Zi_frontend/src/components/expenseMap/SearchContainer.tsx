import { useEffect, useState } from "react";
import { AlignJustify, CalendarCheck } from "lucide-react";
import { Category } from "@/types/category";
import { getCategories } from "@/services/category";
import ReadingGlassesIcon from "@components/svgs/ReadingGlassesIcon";
import CategorySelector from "@components/expenseMap/CategorySelector";

type Props = {
  setSearchParams: (params: {
    keyword: string;
    categoryId: number | null;
    startDate: string;
    endDate: string;
  }) => void;
};

const SearchContainer = ({ setSearchParams }: Props) => {
  const formatToDateStr = (date: Date) => date.toISOString().split("T")[0];
  const today = new Date();
  const todayStr = formatToDateStr(today);
  const oneYearAgo = new Date(today);
  oneYearAgo.setFullYear(oneYearAgo.getFullYear() - 1);
  const [isOpen, setIsOpen] = useState<boolean>(false);
  const [keyword, setKeyword] = useState<string>("");
  const [startDate, setStartDate] = useState<string>(formatToDateStr(oneYearAgo));
  const [endDate, setEndDate] = useState<string>(formatToDateStr(today));
  const [selectedCategory, setSelectedCategory] = useState<number | null>(null);
  const formatDate = (dateStr: string) => dateStr.replaceAll("-", "");
  const [isSearchBarOpen, setIsSearchBarOpen] = useState<boolean>(false);
  const [isDetailOpen, setIsDetailOpen] = useState<boolean>(false);
  const [showDetail, setShowDetail] = useState<boolean>(false);
  const [categories, setCategories] = useState<Category[]>([]);

  const selectedCategoryName =
    categories.find((c) => c.categoryId === selectedCategory)?.categoryName ?? "전체";
  const dateRange = `${startDate.slice(2)} ~ ${endDate.slice(2)}`;

  const handleSearch = () => {
    setSearchParams({
      keyword,
      categoryId: selectedCategory,
      startDate: formatDate(startDate),
      endDate: formatDate(endDate),
    });

    setIsDetailOpen(false); // 상세 옵션 닫기
    setShowDetail(false); // 상세창 애니메이션 상태 닫기
    setIsSearchBarOpen(true);
    setIsOpen(false);
  };

  useEffect(() => {
    // 컴포넌트가 마운트되면 카테고리 데이터 불러오기
    const fetchCategories = async () => {
      try {
        const data = await getCategories(null, 0, "SPEND");
        setCategories(data);
      } catch (err) {
        // console.error("카테고리 불러오기 실패", err);
      }
    };

    fetchCategories();
  }, []);

  return (
    <div className="absolute top-1 left-0 w-full z-10 px-4 py-2">
      {/* 상단 검색창 */}
      <div
        className={`flex items-center h-10 border rounded-full border-primary-500 px-[10px] py-2 shadow-md gap-2 bg-white transition-all duration-300 ease-in-out overflow-hidden ${
          isSearchBarOpen ? "w-full" : "justify-start w-10"
        }`}
      >
        <button
          onClick={() => {
            setIsSearchBarOpen((prev) => {
              if (prev) {
                setIsDetailOpen(false);
              }
              return !prev;
            });
          }}
          className="flex justify-center items-center hover:cursor-pointer shrink-0"
        >
          <ReadingGlassesIcon className="w-5 h-5 text-neutral-500 shrink-0" />
        </button>
        {isSearchBarOpen && (
          <>
            <input
              type="text"
              placeholder="가게명을 입력해주세요"
              className="sm:text-md text-xs flex-grow outline-none min-w-0 max-h-5 "
              onChange={(e) => setKeyword(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter") {
                  e.preventDefault();
                  handleSearch();
                }
              }}
            />
            <div className="sm:text-xs text-neutral-400 ml-2 whitespace-nowrap text-xs">
              {`${selectedCategoryName} | ${dateRange}`}
            </div>
            <div
              onClick={() => {
                if (isDetailOpen) {
                  setIsDetailOpen(false);
                  setTimeout(() => setShowDetail(false), 300);
                } else {
                  setShowDetail(true);
                  setTimeout(() => setIsDetailOpen(true), 0);
                }
              }}
              className="flex justify-center items-center hover:cursor-pointer"
            >
              <AlignJustify className="text-neutral-500 w-5 h-5" />
            </div>
          </>
        )}
      </div>

      {showDetail && (
        <div
          className={`mt-2 bg-white border p-4 rounded-lg shadow-md space-y-4 transition-all duration-300 ease-in-out ${
            isDetailOpen ? "max-h-[1000px] opacity-100" : "max-h-0 opacity-0 p-0"
          } overflow-hidden`}
        >
          {/* 기간 설정 */}
          <div>
            <div className="flex items-center gap-2 mb-1">
              <CalendarCheck className="w-5" />
              <label className="font-semibold whitespace-nowrap">기간 설정</label>
            </div>
            <div className="flex flex-col items-center sm:flex-row gap-2 w-full">
              <input
                type="date"
                value={startDate}
                onChange={(e) => {
                  const newStart = e.target.value;
                  setStartDate(newStart);

                  // 시작일 종료일 유효성 검사
                  if (endDate && newStart > endDate) {
                    setEndDate(newStart);
                  }
                }}
                className="border rounded px-2 py-1 w-full "
                max={todayStr || endDate}
              />
              <span>~</span>
              <input
                type="date"
                value={endDate}
                onChange={(e) => {
                  const newEnd = e.target.value;
                  if (startDate && newEnd < startDate) return;
                  setEndDate(newEnd);
                }}
                className="border rounded px-2 py-1 w-full"
                min={startDate || undefined}
                max={todayStr}
              />
            </div>
          </div>

          {/* 카테고리 */}
          <CategorySelector
            selectedCategory={selectedCategory}
            setSelectedCategory={setSelectedCategory}
          />
          {/* 검색 버튼 */}
          <div className="text-center">
            <button
              onClick={handleSearch}
              className="w-full bg-primary-500 text-white py-2 rounded-xl hover:bg-primary-600 transition"
            >
              검색
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default SearchContainer;
