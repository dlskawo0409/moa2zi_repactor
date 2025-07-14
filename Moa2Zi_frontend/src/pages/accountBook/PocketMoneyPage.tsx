import { useState } from "react";

import NavBar from "@/components/common/NavBar";

import PocketMoneyView from "@/components/pocketMoney/PocketMoneyView";
import PocketMoneySetting from "@/components/pocketMoney/PocketMoneySetting";

const PocketMoneyPage = () => {
  const [isSetting, setIsSetting] = useState<boolean>(false);
  const [thisMonthHave, setThisMonthHave] = useState<boolean>(true);

  return (
    <div>
      <NavBar />
      <div className="flex flex-col w-full mb-5 gap-6">
        {isSetting ? (
          <PocketMoneySetting
            isSetting={isSetting}
            setIsSetting={setIsSetting}
            thisMonthHave={thisMonthHave}
          />
        ) : (
          <PocketMoneyView
            isSetting={isSetting}
            setIsSetting={setIsSetting}
            thisMonthHave={thisMonthHave}
            setThisMonthHave={setThisMonthHave}
          />
        )}
      </div>
    </div>
  );
};

export default PocketMoneyPage;
