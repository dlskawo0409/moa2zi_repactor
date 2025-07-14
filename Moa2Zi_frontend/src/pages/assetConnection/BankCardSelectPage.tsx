import { useEffect, useState } from "react";
import { ArrowLeft, Check } from "lucide-react";

import CommonButton from "@/components/common/CommonButton";
import BankItem from "@/components/assetConnection/BankItem";
import { getAssets, postAssets } from "@/services/finance";
import { Bank, CardcardIssuer } from "@/types/bank";

type BankCardSelectPageProps = {
  setPage: (page: number) => void;
};

const tabs = ["은행", "카드"];

const BankCardSelectPage = ({ setPage }: BankCardSelectPageProps) => {
  const [selectedTab, setSelectedTab] = useState<number>(0);

  const [bankList, setBankList] = useState<Bank[]>([]);
  const [cardList, setCardList] = useState<CardcardIssuer[]>([]);
  const [selectedBanks, setSelectedBanks] = useState<string[]>([]);
  const [selectedCards, setSelectedCards] = useState<string[]>([]);

  const handleTabClick = (index: number) => {
    setSelectedTab(index);
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  // 은행 선택
  const handleBankClick = (code: string) => {
    setSelectedBanks((prev) =>
      prev.includes(code) ? prev.filter((c) => c !== code) : [...prev, code],
    );
  };

  // 은행 전체선택
  const handleSelectAllBanks = () => {
    if (
      selectedBanks.filter((code) => bankList.some((b) => b.bankCode === code)).length ===
      bankList.length
    ) {
      setSelectedBanks((prev) => prev.filter((code) => !bankList.some((b) => b.bankCode === code)));
    } else {
      setSelectedBanks((prev) => [
        ...prev.filter((code) => !bankList.some((b) => b.bankCode === code)),
        ...bankList.map((b) => b.bankCode),
      ]);
    }
  };

  // 카드 선택
  const handleCardClick = (code: string) => {
    setSelectedCards((prev) =>
      prev.includes(code) ? prev.filter((c) => c !== code) : [...prev, code],
    );
  };

  // 카드 전체선택
  const handleSelectAllCards = () => {
    if (
      selectedCards.filter((code) => cardList.some((c) => c.cardIssuerCode === code)).length ===
      cardList.length
    ) {
      setSelectedCards((prev) =>
        prev.filter((code) => !cardList.some((c) => c.cardIssuerCode === code)),
      );
    } else {
      setSelectedCards((prev) => [
        ...prev.filter((code) => !cardList.some((c) => c.cardIssuerCode === code)),
        ...cardList.map((c) => c.cardIssuerCode),
      ]);
    }
  };

  // 1원 송금 단계로 이동
  const handleConnect = async () => {
    try {
      // console.log("선택된 은행:", selectedBanks);
      // console.log("선택된 카드:", selectedCards);

      await postAssets({
        bankCodeList: selectedBanks,
        cardIssuerCodeList: selectedCards,
      });

      setPage(2);
    } catch (error) {
      // console.error(" 연결 실패:", error);
    }
  };

  useEffect(() => {
    const fetchBanks = async () => {
      const assetsData = await getAssets();

      setBankList(assetsData.bankList);
      setCardList(assetsData.cardIssuerList);

      setSelectedBanks(assetsData.bankList.map((b) => b.bankCode));
      setSelectedCards(assetsData.cardIssuerList.map((c) => c.cardIssuerCode));
    };

    fetchBanks();
  }, []);

  return (
    <>
      <div className="sticky top-0 flex items-center w-full h-[55px] px-5 bg-white">
        <div className="cursor-pointer" onClick={() => setPage(0)}>
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

      {/* 은행/카드사 선택 */}
      {selectedTab === 0 ? (
        <div className="px-5 pt-4">
          <div className="flex justify-between h-8 mb-4">
            <div className="text-lg font-bold flex items-center gap-2">
              은행
              {selectedBanks.length > 0 && (
                <div className="w-8 h-8 flex justify-center items-center bg-primary-500 text-white rounded-full">
                  {selectedBanks.length}
                </div>
              )}
            </div>
            <div
              className={`flex items-center gap-1 cursor-pointer ${
                selectedBanks.filter((code) => bankList.some((b) => b.bankCode === code)).length ===
                bankList.length
                  ? "text-primary-500"
                  : "text-neutral-500"
              }`}
              onClick={handleSelectAllBanks}
            >
              <Check />
              전체선택 (은행)
            </div>
          </div>
          <div className="flex flex-wrap justify-between gap-2">
            {bankList.map(({ bankCode, bankName, Icon }) => (
              <BankItem
                key={bankCode}
                name={bankName}
                icon={Icon ? <Icon /> : null}
                isSelected={selectedBanks.includes(bankCode)}
                onClick={() => handleBankClick(bankCode)}
              />
            ))}
          </div>
        </div>
      ) : (
        <div className="px-5 pt-4">
          <div className="flex justify-between h-8 mb-4">
            <div className="text-lg font-bold flex items-center gap-2">
              카드사
              {selectedCards.length > 0 && (
                <div className="w-8 h-8 flex justify-center items-center bg-primary-500 text-white rounded-full">
                  {selectedCards.length}
                </div>
              )}
            </div>
            <div
              className={`flex items-center gap-1 cursor-pointer ${
                selectedCards.filter((code) => cardList.some((c) => c.cardIssuerCode === code))
                  .length === cardList.length
                  ? "text-primary-500"
                  : "text-neutral-500"
              }`}
              onClick={handleSelectAllCards}
            >
              <Check />
              전체선택 (카드)
            </div>
          </div>
          <div className="flex flex-wrap justify-between gap-2">
            {cardList.map(({ cardIssuerCode, cardIssuerName, Icon }) => (
              <BankItem
                key={cardIssuerCode}
                name={cardIssuerName}
                icon={Icon ? <Icon /> : null}
                isSelected={selectedCards.includes(cardIssuerCode)}
                onClick={() => handleCardClick(cardIssuerCode)}
              />
            ))}
          </div>
        </div>
      )}

      {/* 다음 버튼 */}
      {(selectedBanks.length > 0 || selectedCards.length > 0) && (
        <div className="sticky bottom-0 left-0 p-5 mt-auto bg-white">
          <CommonButton variant="primary" className="w-full" onClick={handleConnect}>
            연결하기
          </CommonButton>
        </div>
      )}
    </>
  );
};

export default BankCardSelectPage;
