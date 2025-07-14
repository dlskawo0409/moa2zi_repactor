import { useState } from "react";

import AssetConnectionMainPage from "@/pages/assetConnection/AssetConnectionMainPage";
import BankCardSelectPage from "@/pages/assetConnection/BankCardSelectPage";
import AccountVerificationPage from "@/pages/assetConnection/AccountVerificationPage";
import AssetConnectionCompletePage from "@/pages/assetConnection/AssetConnectionCompletePage";

const AssetConnectionPage = () => {
  const [page, setPage] = useState<number>(0);

  return (
    <>
      {page === 0 && <AssetConnectionMainPage setPage={setPage} />}

      {page === 1 && <BankCardSelectPage setPage={setPage} />}

      {page === 2 && <AccountVerificationPage setPage={setPage} />}

      {page === 3 && <AssetConnectionCompletePage />}
    </>
  );
};

export default AssetConnectionPage;
