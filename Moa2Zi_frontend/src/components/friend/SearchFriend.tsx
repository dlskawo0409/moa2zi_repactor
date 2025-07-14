import { useState, useRef, useCallback, KeyboardEvent } from "react";
import FriendItem from "@/components/friend/FriendItem";
import FriendItemSkeleton from "@/components/friend/FriendItemSkeleton";
import { Input } from "@/components/ui/input";
import { getMembers } from "@/services/member";
import { SearchMember } from "@/types/friend";

const SearchFriend = () => {
  const [search, setSearch] = useState<string>("");
  const [searchMembers, setSearchMembers] = useState<SearchMember[]>([]);
  const [pageInfo, setPageInfo] = useState<{
    next: number | null;
    friendsOrder: number | null;
    hasNext: boolean;
  }>({
    next: null,
    friendsOrder: null,
    hasNext: false,
  });
  const [isLoading, setIsLoading] = useState<boolean>(false);

  const observer = useRef<IntersectionObserver | null>(null);
  const lastItemRef = useCallback(
    (node: HTMLDivElement | null) => {
      if (isLoading) return;
      if (observer.current) observer.current.disconnect();

      observer.current = new IntersectionObserver(([entry]) => {
        if (entry.isIntersecting && pageInfo.hasNext) {
          loadMore();
        }
      });

      if (node) observer.current.observe(node);
    },
    [isLoading, pageInfo],
  );

  const handleKeyDown = async (e: KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter" && search.trim()) {
      setIsLoading(true);
      try {
        const res = await getMembers({
          nickname: search,
          size: 10,
          next: null,
          friendsOrder: null,
        });

        setSearchMembers(res.memberList);
        setPageInfo({
          next: res.next,
          friendsOrder: res.friendsOrder,
          hasNext: res.hasNext,
        });
      } catch (error) {
        // console.error(error);
      } finally {
        setIsLoading(false);
      }
    }
  };

  // API 추가 호출 (무한 스크롤)
  const loadMore = async () => {
    setIsLoading(true);
    try {
      const res = await getMembers({
        nickname: search,
        size: 10,
        next: pageInfo.next || null,
        friendsOrder: pageInfo.friendsOrder ?? null,
      });

      setSearchMembers((prev) => [...prev, ...res.memberList]);
      setPageInfo({
        next: res.next,
        friendsOrder: res.friendsOrder,
        hasNext: res.hasNext,
      });
    } catch (error) {
      // console.error(error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <>
      <div className="mx-5">
        <Input
          placeholder="검색어를 입력해주세요"
          className="flex-1 border-2 border-neutral-200 bg-neutral-50 text-sm py-2 focus:border-primary-500 focus-visible:ring-primary-500"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          onKeyDown={handleKeyDown}
        />
      </div>

      <div className="flex flex-col gap-4">
        {searchMembers
          .filter((friend) => friend.friendsOrder === 2)
          .map((friend, idx, filtered) => {
            const isLast = idx === filtered.length - 1;

            return (
              <div key={friend.memberId} ref={isLast ? lastItemRef : null}>
                <FriendItem
                  memberid={friend.memberId}
                  type="search"
                  profileIcon={friend.profileImage}
                  nickname={friend.nickname}
                />
              </div>
            );
          })}

        {isLoading && <FriendItemSkeleton />}
      </div>
    </>
  );
};

export default SearchFriend;
