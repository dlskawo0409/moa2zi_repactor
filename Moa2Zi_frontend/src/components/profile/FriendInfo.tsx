import { useEffect, useState } from "react";

import VerticalLine from "@/components/common/VerticalLine";
import { getFriendList } from "@/services/friend";
import { useUserInfo } from "@/hooks/useUserInfo";

const FriendInfo = () => {
  const { data, isLoading } = useUserInfo();

  const [friendCount, setFriendCount] = useState<number>(0);
  const [pendingCount, setPendingCount] = useState<number>(0);
  const [waitingCount, setWaitingCount] = useState<number>(0);

  useEffect(() => {
    const fetchFriends = async () => {
      if (isLoading || !data?.memberId) return;

      try {
        const allData = await getFriendList({
          requestId: data.memberId,
          acceptId: data.memberId,
          status: "ACCEPTED",
          size: 0,
        });
        const pendingData = await getFriendList({
          requestId: data.memberId,
          status: "PENDING",
          size: 0,
        });
        const waitingData = await getFriendList({
          acceptId: data.memberId,
          status: "PENDING",
          size: 0,
        });

        setFriendCount(allData.total);
        setPendingCount(pendingData.total);
        setWaitingCount(waitingData.total);
      } catch (error) {
        // console.error("친구 목록을 불러오는 중 오류 발생:", error);
      }
    };

    fetchFriends();
  }, [isLoading]);

  return (
    <div className="flex justify-center gap-5 h-10 pc:gap-10 text-md">
      <div className="flex flex-col w-20 justify-center items-center">
        <div>친구</div>
        <div>{friendCount}</div>
      </div>
      <VerticalLine />
      <div className="flex flex-col w-20 justify-center items-center">
        <div>요청중</div>
        <div>{pendingCount}</div>
      </div>
      <VerticalLine />
      <div className="flex flex-col w-20 justify-center items-center">
        <div>대기중</div>
        <div>{waitingCount}</div>
      </div>
    </div>
  );
};

export default FriendInfo;
