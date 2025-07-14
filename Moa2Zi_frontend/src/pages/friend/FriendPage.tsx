import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { ArrowLeft } from "lucide-react";

import AllFriend from "@/components/friend/AllFriend";
import RequestFriend from "@/components/friend/RequestFriend";
import RequestedFriend from "@/components/friend/RequestedFriend";
import SearchFriend from "@/components/friend/SearchFriend";

const tabs = ["모두", "요청 중", "대기 중", "친구 추가"];
const tabContents = [
  <div key="0">
    <AllFriend />
  </div>,
  <div key="1" className="flex flex-col gap-4">
    <RequestFriend />
  </div>,
  <div key="2" className="flex flex-col gap-4">
    <RequestedFriend />
  </div>,
  <div key="3" className="flex flex-col gap-4">
    <SearchFriend />
  </div>,
];

const FriendPage = () => {
  const navigate = useNavigate();

  const [selectedTab, setSelectedTab] = useState<number>(0);

  return (
    <>
      <div className="sticky top-0 flex items-center w-full h-[55px] px-5 bg-white ">
        <div className="cursor-pointer" onClick={() => navigate(-1)}>
          <ArrowLeft />
        </div>
        <div className="absolute left-1/2 -translate-x-1/2 text-xl font-bold">친구</div>
      </div>

      <div className="relative flex gap-1 mb-5 border-b">
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
          style={{ width: "25%", left: `${selectedTab * 25}%` }}
        />
      </div>

      <div className="">{tabContents[selectedTab]}</div>
    </>
  );
};

export default FriendPage;
