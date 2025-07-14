import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import CommonButton from "@/components/common/CommonButton";
import CreditCards from "@/components/profile/CreditCards";
import AccountItem from "@/components/profile/AccountItem";
import AccountItemSkeleton from "@/components/profile/AccountItemSkeleton";
import { getMemberAccounts } from "@/services/finance";
import { MemberAccount } from "@/types/account";

const MyAccount = () => {
  const navigate = useNavigate();

  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [accounts, setAccounts] = useState<MemberAccount[]>([]);

  const handleAssetConnectionClick = () => {
    navigate("/asset-connection");
  };

  useEffect(() => {
    const fetchAccounts = async () => {
      try {
        const data = await getMemberAccounts();
        setAccounts(data);
      } catch (error) {
        // console.error("자산 불러오기 실패:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchAccounts();
  }, []);

  return (
    <>
      <div className="flex flex-col mb-5">
        <div className="flex justify-center my-5">
          <CreditCards />
        </div>
        <div className="flex flex-col gap-4">
          {isLoading
            ? Array.from({ length: 2 }).map((_, idx) => <AccountItemSkeleton key={idx} />)
            : accounts.map((account, idx) => (
                <AccountItem
                  key={idx}
                  bankCode={account.bankCode}
                  accountName={account.accountName}
                  accountNo={account.accountNo}
                />
              ))}
        </div>
      </div>

      <div className="mb-5 mx-5">
        <CommonButton variant="primary" className="w-full" onClick={handleAssetConnectionClick}>
          쥐갑 연결
        </CommonButton>
      </div>
    </>
  );
};

export default MyAccount;
