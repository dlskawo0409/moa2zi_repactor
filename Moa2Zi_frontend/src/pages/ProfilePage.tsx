import { useState } from "react";
import { useNavigate } from "react-router-dom";
import UserInfo from "@/components/profile/UserInfo";
import FriendInfo from "@/components/profile/FriendInfo";
import MyAccount from "@/components/profile/MyAccount";
import PrizeLocker from "@/components/profile/PrizeLocker";
import GameHistory from "@/components/profile/GameHistory";

const tabs = ["내 쥐갑", "상장 보관함", "게임 히스토리"];
const tabContents = [
  <div key="0">
    <MyAccount />
  </div>,
  <div key="1">
    <PrizeLocker />
  </div>,
  <div key="2">
    <GameHistory />
  </div>,
];

const ProfilePage = () => {
  const navigate = useNavigate();

  const [selectedTab, setSelectedTab] = useState<number>(0);

  return (
    <div className="flex flex-col">
      <div className="flex flex-col m-5 gap-4">
        <UserInfo />

        <div onClick={() => navigate("/profile/friend")} className="cursor-pointer">
          <FriendInfo />
        </div>
      </div>
      <div className="relative flex gap-1 border-b">
        {tabs.map((tab, index) => (
          <div
            key={index}
            className={`flex-1 p-2 text-center cursor-pointer ${
              selectedTab === index ? "font-bold text-primary-500" : "text-neutral-500"
            }`}
            onClick={() => setSelectedTab(index)}
          >
            {tab}
          </div>
        ))}

        <div
          className="absolute bottom-0 h-1 bg-primary-500 transition-all duration-300"
          style={{ width: "33.33%", left: `${selectedTab * 33.33}%` }}
        />
      </div>

      <div className="">{tabContents[selectedTab]}</div>
    </div>
  );
};

export default ProfilePage;
