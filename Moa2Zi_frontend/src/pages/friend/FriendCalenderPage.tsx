import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { ArrowLeft } from "lucide-react";
import FriendCalender from "@/components/friend/FriendCalender";
import { getFriendInfo } from "@/services/friend";
import { UserInfo } from "@/types/auths";
import { getProfileIcon } from "@/utils/getProfileIcon";
import { getFriendList } from "@/services/lounge";
import ChillMouseIcon from "@components/svgs/ChillMouseIcon";

const FriendCalenderPage = () => {
  const navigate = useNavigate();
  const { memberId } = useParams();

  const [friendInfo, setFriendInfo] = useState<UserInfo>({
    alarm: false,
    birthday: "",
    createdAt: "",
    disclosure: "",
    gender: "",
    memberId: 0,
    nickname: "",
    profileImage: "",
    updateAt: "",
    username: "",
    theyAreFriend: false,
  });

  const fetchData = async () => {
    try {
      const response = await getFriendInfo(memberId);
      setFriendInfo(response);
      // console.log("친구", response);
    } catch (error) {
      // console.log(error);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const [friendCount, setFriendCount] = useState<number>(0);

  useEffect(() => {
    const fetchFriends = async () => {
      try {
        const allData = await getFriendList({
          requestId: Number(memberId),
          acceptId: Number(memberId),
          status: "ACCEPTED",
          size: 0,
        });

        setFriendCount(allData.data.total);
        // console.log(allData, "이거");
      } catch (error) {
        // console.error("친구 목록을 불러오는 중 오류 발생:", error);
      }
    };

    fetchFriends();
  }, []);

  const Icon = getProfileIcon(friendInfo.profileImage);

  return (
    <>
      <div className="flex items-center w-full h-[55px] px-5 border-b border-neutral-200">
        <div className="cursor-pointer" onClick={() => navigate(-1)}>
          <ArrowLeft />
        </div>
      </div>
      <div className="flex flex-col m-5 border-b border-neutral-200">
        <div className="flex justify-center">
          <div className="flex flex-col gap-2 items-center">
            <div className="w-12 h-12">{Icon && <Icon className="w-full h-full" />}</div>
            <div className="flex font-bold text-center text-xl">{friendInfo.nickname}</div>
          </div>
        </div>
        <div
          onClick={() => navigate(`/profile/friend/friendinfo/${memberId}`)}
          className="cursor-pointer py-3"
        >
          <div className="flex justify-center items-center gap-3">
            <div>친구</div>
            <div>{friendCount}</div>
          </div>
        </div>
      </div>
      {(friendInfo.disclosure === "ALL" ||
        (friendInfo.disclosure === "FRIEND" && friendInfo.theyAreFriend)) && (
        <FriendCalender nickname={friendInfo.nickname} />
      )}
      {friendInfo.disclosure === "ONLY_ME" && (
        <div className="flex flex-col gap-10 mt-10 justify-center text-center">
          <div className="flex justify-center w-full gap-2 text-xl pc:text-2xl">
            <div className="text-primary-500 font-bold">{friendInfo.nickname}</div>
            <div className="font-semibold">님의 소비 달력</div>
          </div>
          <ChillMouseIcon className="size-20 self-center" />

          <div className="flex gap-1 justify-center text-center">
            <div>~ 비공개 상태쥐 ~</div>
          </div>
        </div>
      )}
    </>
  );
};

export default FriendCalenderPage;
