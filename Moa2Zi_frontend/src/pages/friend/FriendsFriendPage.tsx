import { useEffect, useState, useRef, useCallback } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { ArrowLeft } from "lucide-react";

import { getFriendList } from "@/services/friend";
import { FriendInfo } from "@/types/friend";
import FriendItemSkeleton from "@components/friend/FriendItemSkeleton";
import { getProfileIcon } from "@/utils/getProfileIcon";
import CommonButton from "@components/common/CommonButton";
import { useUserInfo } from "@/hooks/useUserInfo";

const tabs = [`친구`];

const FriendsFriendPage = () => {
  const navigate = useNavigate();
  const { memberId } = useParams();
  const { data } = useUserInfo();

  const [selectedTab, setSelectedTab] = useState<number>(0);
  const [friendList, setFriendList] = useState<FriendInfo[]>([]);
  const [pageInfo, setPageInfo] = useState({ next: 0, hasNext: true });
  const [isLoading, setIsLoading] = useState<boolean>(false);

  const observer = useRef<IntersectionObserver | null>(null);

  const parseFriendData = (friends: any[]): FriendInfo[] => {
    return friends
      .map((friend) => {
        const isRequest = friend.requestMember.memberId === Number(memberId);
        const targetMember = isRequest ? friend.acceptMember : friend.requestMember;
        return { ...targetMember, friendId: friend.friendId };
      })
      .filter((friend) => friend.memberId !== data?.memberId); // 본인 제외
  };

  const loadFriendList = async (isInitial = false) => {
    if (!memberId || (!isInitial && !pageInfo.hasNext)) return;

    setIsLoading(true);
    try {
      const res = await getFriendList({
        requestId: Number(memberId),
        acceptId: Number(memberId),
        status: "ACCEPTED",
        size: 10,
        next: isInitial ? undefined : pageInfo.next,
      });

      const parsedData = parseFriendData(res.friendList);
      setFriendList((prev) => (isInitial ? parsedData : [...prev, ...parsedData]));
      setPageInfo({ next: res.next, hasNext: res.hasNext });
    } catch (err) {
      // console.error("친구 목록 로딩 실패:", err);
    } finally {
      setIsLoading(false);
    }
  };

  const lastItemRef = useCallback(
    (node: HTMLDivElement | null) => {
      if (isLoading || !pageInfo.hasNext) return;
      if (observer.current) observer.current.disconnect();

      observer.current = new IntersectionObserver(([entry]) => {
        if (entry.isIntersecting) {
          loadFriendList();
        }
      });

      if (node) observer.current.observe(node);
    },
    [isLoading, pageInfo.hasNext],
  );

  const handleViewProfile = (memberId: number) => {
    navigate(`/profile/friend/${memberId}`);
  };

  useEffect(() => {
    if (data) {
      loadFriendList(true);
    }
  }, [data]);

  return (
    <>
      <div className="flex items-center w-full h-[55px] px-5">
        <button onClick={() => navigate(-1)}>
          <ArrowLeft />
        </button>
      </div>

      <div className="relative flex gap-1 mb-5 border-b border-primary-500">
        {tabs.map((tab, idx) => (
          <div
            key={idx}
            className={`flex-1 p-2 text-center cursor-pointer ${
              selectedTab === idx ? "font-bold text-primary-500" : "text-neutral-500"
            }`}
            onClick={() => setSelectedTab(idx)}
          >
            {tab}
          </div>
        ))}
      </div>

      <div className="flex flex-col gap-4">
        {friendList.map((friend, idx) => {
          const isLast = idx === friendList.length - 1;
          const Icon = getProfileIcon(friend.profileImage);

          return (
            <div key={friend.friendId} ref={isLast ? lastItemRef : null}>
              <div className="flex items-center gap-2 mx-5">
                <div className="flex justify-between w-full gap-4">
                  <div className="flex gap-3 items-center">
                    <div className="w-12 h-12">
                      {Icon && <Icon className="w-full h-full border rounded-full" />}
                    </div>
                    <span className="font-bold">{friend.nickname}</span>
                  </div>
                  <CommonButton
                    variant="positive"
                    className="pc:px-10"
                    onClick={() => handleViewProfile(friend.memberId)}
                  >
                    프로필 보기
                  </CommonButton>
                </div>
              </div>
            </div>
          );
        })}

        {isLoading && <FriendItemSkeleton />}

        {!isLoading && friendList.length === 0 && (
          <div className="text-sm text-center text-gray-400">친구가 없어요</div>
        )}
      </div>
    </>
  );
};

export default FriendsFriendPage;
