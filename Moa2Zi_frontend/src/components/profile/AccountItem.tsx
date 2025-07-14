import { getBankInfo } from "@/utils/getAssetInfo";

interface AccountItemProps {
  bankCode: string;
  accountName: string;
  accountNo: string;
}

const AccountItem = ({ bankCode, accountName, accountNo }: AccountItemProps) => {
  const { name: bankName, Icon: BankIcon } = getBankInfo(bankCode);

  return (
    <div className="flex w-full px-5 gap-2">
      <div>{BankIcon && <BankIcon className="w-12 h-12 rounded-full" />}</div>
      <div className="flex flex-col">
        <div>{accountName}</div>
        <div className="text-neutral-400 text-sm">
          {bankName} {accountNo}
        </div>
      </div>
    </div>
  );
};

export default AccountItem;
