import { useNavigate } from "react-router-dom";
import { ArrowLeft } from "lucide-react";

import CommonButton from "@components/common/CommonButton";
import Assets from "@/components/assetConnection/Assets";
import { useUserInfo } from "@/hooks/useUserInfo";

type AssetConnectionMainPage = {
  setPage: (page: number) => void;
};

const AssetConnectionMainPage = ({ setPage }: AssetConnectionMainPage) => {
  const { data } = useUserInfo();
  const navigate = useNavigate();

  return (
    <>
      <div className="sticky top-0 flex items-center w-full h-[55px] px-5 bg-white border-b-[1px]">
        <div className="cursor-pointer" onClick={() => navigate(-1)}>
          <ArrowLeft />
        </div>
        <div className="absolute left-1/2 -translate-x-1/2 text-xl font-bold">쥐갑 연결</div>
      </div>

      <div
        className={`flex flex-col justify-center mt-5 text-xl text-center ${data?.nickname ? "" : "invisible"}`}
      >
        <div className="flex justify-center">
          <div className="text-primary-500 font-bold">{data?.nickname}</div>
          <div>님의 자산을</div>
        </div>
        <div>한 번에 찾아보세요</div>
      </div>

      <div className="my-auto overflow-hidden">
        <Assets />
      </div>

      <div className="sticky bottom-0 left-0 p-5 mt-auto">
        <CommonButton variant="primary" className="w-full" onClick={() => setPage(1)}>
          쥐갑 연결하기
        </CommonButton>
      </div>
    </>
  );
};

export default AssetConnectionMainPage;
