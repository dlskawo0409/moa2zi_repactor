import { useState, useEffect, useCallback, useRef } from "react";
import LoungeStartButton from "@components/lounge/LoungeStartButton";
import LoungeCard from "@components/lounge/LoungeCard";
import LoungeSearchBar from "@components/lounge/LoungeSearchBar";
import { Skeleton } from "@components/ui/skeleton";
import { getLoungeList } from "@/services/lounge";
import { LoungeStatus, LoungeList as LoungeListType, Lounge } from "@/types/lounge";
import UnbelievableMouseIcon from "@components/svgs/UnbelievableMouseIcon";

type FilterMode = "TITLE" | "NICKNAME";

const LoungeMainPage = () => {
  const [filterMode, setFilterMode] = useState<FilterMode>("TITLE"); // 검색 기준
  const [keyword, setKeyword] = useState<string>(""); // 검색어
  const [loungeList, setLoungeList] = useState<Lounge[]>([]);
  const [next, setNext] = useState<number | null>(null); // 다음 페이지 요청 cursor 값
  const [loungeStatus, setLoungeStatus] = useState<LoungeStatus | null>(); // 다음 페이지 요청 loungeStatus 값
  const [hasNext, setHasNext] = useState<boolean>(true); // 다음 페이지 있는지 여부
  const [loading, setLoading] = useState<boolean>(false); // 데이터 로딩중 여부

  const observerRef = useRef<HTMLDivElement | null>(null);

  const fetchData = useCallback(
    async (isNewSearch = false) => {
      if (loading || (!isNewSearch && !hasNext)) {
        return;
      }
      setLoading(true);

      try {
        const response: { data: LoungeListType } = await getLoungeList({
          searchType: filterMode,
          keyword: keyword,
          next: isNewSearch ? null : next,
          loungeStatus: isNewSearch ? null : loungeStatus,
          size: 10,
        });

        // console.log(response.data);
        if (isNewSearch) {
          setLoungeList(response.data.loungeList);
          // setLoungeStatus(null);
        } else {
          setLoungeList((prev) => [...prev, ...response.data.loungeList]);
        }

        setHasNext(response.data.hasNext);
        setNext(response.data.next);
        setLoungeStatus(response.data.loungeStatus);
      } catch (error) {
        // console.error(error);
      } finally {
        setLoading(false);
      }
    },
    [filterMode, keyword, next, loungeStatus, hasNext, loading],
  );

  useEffect(() => {
    fetchData(true);
  }, [filterMode]);

  useEffect(() => {
    if (!observerRef.current || !hasNext) {
      return;
    }

    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting && !loading) {
          fetchData();
        }
      },
      { threshold: 1.0 }, // 감시 중인 요소가 100% 화면에 보일 때만 실행
    );

    // 감시 시작
    observer.observe(observerRef.current);

    // cleanup 함수 : 컴포넌트가 언마운트 되거나 useEffect가 다시 실행될 때 기존 감시 중이던 요소 감시 중단
    return () => {
      if (observerRef.current) observer.unobserve(observerRef.current);
    };
  }, [fetchData, hasNext, loading]);

  const handleSearch = () => {
    setNext(null);
    setHasNext(true);
    fetchData(true);
  };

  return (
    <div className="relative flex flex-col p-5 gap-3">
      <LoungeStartButton />
      <LoungeSearchBar
        filterMode={filterMode}
        setFilterMode={setFilterMode}
        keyword={keyword}
        setKeyword={setKeyword}
        handleSearch={handleSearch}
      />
      {loading && (
        <>
          <Skeleton className="flex w-full h-20 bg-neutral-200 rounded-md" />
          <Skeleton className="flex w-full h-20 bg-neutral-200 rounded-md" />
          <Skeleton className="flex w-full h-20 bg-neutral-200 rounded-md" />
          <Skeleton className="flex w-full h-20 bg-neutral-200 rounded-md" />
          <Skeleton className="flex w-full h-20 bg-neutral-200 rounded-md" />
        </>
      )}
      {!loading && loungeList.length === 0 && (
        <div className="flex flex-col items-center mt-10">
          <UnbelievableMouseIcon className="size-20" />
          <div className="text-center text-gray-500 py-6">참여중인 라운쥐가 없습니다.</div>
        </div>
      )}

      {loungeList.map((lounge) => (
        <LoungeCard key={lounge.loungeId} lounge={lounge} />
      ))}

      <div ref={observerRef} className="h-8" />
    </div>
  );
};

export default LoungeMainPage;
