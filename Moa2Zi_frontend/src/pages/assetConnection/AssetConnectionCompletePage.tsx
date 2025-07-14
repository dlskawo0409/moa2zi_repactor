import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { ArrowLeft } from "lucide-react";

import CommonButton from "@/components/common/CommonButton";
import AssetItem from "@/components/assetConnection/AssetItem";
import AssetLoading from "@/components/assetConnection/AssetLoading";
import { getAssetsFetch } from "@/services/finance";
import { getBankInfo, getCardIssuerInfo } from "@/utils/getAssetInfo";
import { Account, Card } from "@/types/asset";

const tabs = ["계좌", "카드"];

const AssetConnectionCompletePage = () => {
  const navigate = useNavigate();

  const [loading, setLoading] = useState<boolean>(true);
  const [selectedTab, setSelectedTab] = useState<number>(0);

  const [accountList, setAccountList] = useState<Account[]>([]);
  const [cardList, setCardList] = useState<Card[]>([]);

  const handleTabClick = (index: number) => {
    setSelectedTab(index);
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  useEffect(() => {
    const fetchAssets = async () => {
      try {
        const [result] = await Promise.all([
          getAssetsFetch(),
          new Promise((resolve) => setTimeout(resolve, 2000)),
        ]);

        setAccountList(result?.accountList ?? []);
        setCardList(result?.cardList ?? []);
      } catch (error) {
        setAccountList([]);
        setCardList([]);
      } finally {
        setLoading(false);
      }
    };

    fetchAssets();
  }, []);

  if (loading) {
    return <AssetLoading />;
  }

  return (
    <>
      <div className="sticky top-0 flex items-center w-full h-[55px] px-5 bg-white">
        <div className="cursor-pointer" onClick={() => navigate(-1)}>
          <ArrowLeft />
        </div>
        <div className="absolute left-1/2 -translate-x-1/2 text-xl font-bold">쥐갑 연결</div>
      </div>

      {/* 상단 탭 */}
      <div className="sticky top-[54px] bg-white z-10 flex gap-1 border-b">
        {tabs.map((tab, index) => (
          <div
            key={index}
            className={`flex-1 p-2 text-center cursor-pointer ${
              selectedTab === index ? "font-bold text-primary-500" : "text-neutral-500"
            }`}
            onClick={() => handleTabClick(index)}
          >
            {tab}
          </div>
        ))}

        <div
          className="absolute bottom-0 h-1 bg-primary-500 transition-all duration-300"
          style={{ width: "50%", left: `${selectedTab * 50}%` }}
        />
      </div>

      <div className="p-5">
        {selectedTab === 0 && (
          <>
            <p className="text-sm text-gray-600 mb-4">
              총 {accountList.length}개의 계좌를 발견했어요.
            </p>
            {accountList.map((account, index) => {
              const { name, Icon } = getBankInfo(account.bankCode);

              return (
                <AssetItem
                  key={index}
                  Icon={Icon ?? undefined}
                  name={name}
                  title={account.accountName}
                  number={account.accountNo}
                />
              );
            })}
          </>
        )}

        {selectedTab === 1 && (
          <>
            <p className="text-sm text-gray-600 mb-4">
              총 {cardList.length}개의 카드를 발견했어요.
            </p>
            {cardList.map((card, index) => {
              const { name, Icon } = getCardIssuerInfo(card.cardIssuerCode);

              return (
                <AssetItem
                  key={index}
                  Icon={Icon ?? undefined}
                  name={name}
                  title={card.cardName}
                  number={card.cardNo}
                />
              );
            })}
          </>
        )}
      </div>

      <div className="sticky bottom-0 left-0 p-5 mt-auto">
        <CommonButton variant="primary" className="w-full" onClick={() => navigate("/profile")}>
          연결 완료
        </CommonButton>
      </div>
    </>
  );
};

export default AssetConnectionCompletePage;
