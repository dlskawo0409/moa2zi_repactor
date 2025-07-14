import { useEffect, useState, useRef, useCallback } from "react";

import FriendItem from "@/components/friend/FriendItem";
import FriendItemSkeleton from "@/components/friend/FriendItemSkeleton";
import { getFriendList } from "@/services/friend";
import { useUserInfo } from "@/hooks/useUserInfo";
import { FriendInfo } from "@/types/friend";

const AllFriend = () => {
  const { data, isLoading: userLoading } = useUserInfo();
  const [friendList, setFriendList] = useState<FriendInfo[]>([]);
  const [pageInfo, setPageInfo] = useState<{
    next: number | null;
    hasNext: boolean;
  }>({
    next: 0,
    hasNext: true,
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

  useEffect(() => {
    if (!userLoading && data?.memberId) {
      loadInitial();
    }
  }, [userLoading, data?.memberId]);

  // 최초 API 요청
  const loadInitial = async () => {
    setIsLoading(true);
    try {
      const res = await getFriendList({
        requestId: data!.memberId,
        acceptId: data!.memberId,
        status: "ACCEPTED",
        size: 10,
      });

      const friendData = res.friendList.map((friend: any) => {
        const isRequest = friend.requestMember.memberId === data!.memberId;
        const targetMember = isRequest ? friend.acceptMember : friend.requestMember;

        return {
          ...targetMember,
          friendId: friend.friendId,
        };
      });

      setFriendList(friendData);
      setPageInfo({ next: res.next, hasNext: res.hasNext });
    } catch (error) {
      // console.error("로딩 실패:", error);
    } finally {
      setIsLoading(false);
    }
  };

  // API 추가 호출 (무한 스크롤)
  const loadMore = async () => {
    if (!data?.memberId || !pageInfo.hasNext) return;

    setIsLoading(true);
    try {
      const res = await getFriendList({
        requestId: data.memberId,
        acceptId: data.memberId,
        status: "ACCEPTED",
        size: 10,
        next: pageInfo.next ?? null,
      });

      const friendData = res.friendList.map((friend: any) => {
        const isRequest = friend.requestMember.memberId === data.memberId;
        const targetMember = isRequest ? friend.acceptMember : friend.requestMember;

        return {
          ...targetMember,
          friendId: friend.friendId,
        };
      });

      setFriendList((prev) => [...prev, ...friendData]);
      setPageInfo({ next: res.next, hasNext: res.hasNext });
    } catch (error) {
      // console.error("로딩 실패:", error);
    } finally {
      setIsLoading(false);
    }
  };

  // 친구 삭제시 호출
  const handleDeleteFriend = (friendId: number) => {
    setFriendList((prev) => prev.filter((friend) => friend.friendId !== friendId));
  };

  return (
    <div className="flex flex-col gap-4">
      {friendList.map((friend, idx) => {
        const isLast = idx === friendList.length - 1;
        return (
          <div key={friend.friendId} ref={isLast ? lastItemRef : null}>
            <FriendItem
              friendId={friend.friendId}
              memberid={friend.memberId}
              type="friend"
              profileIcon={friend.profileImage}
              nickname={friend.nickname}
              onDeleteSuccess={handleDeleteFriend}
            />
          </div>
        );
      })}

      {isLoading && <FriendItemSkeleton />}

      {!isLoading && friendList.length === 0 && (
        <div className="text-sm text-center text-gray-400">친구가 없어요</div>
      )}
    </div>
  );
};

export default AllFriend;
