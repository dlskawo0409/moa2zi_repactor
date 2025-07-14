import { categoryImages } from "@/constants/categoryImages";
import { TransactionItem } from "@/types/transaction";

interface TransactionProps {
  transaction: TransactionItem;
}

const Transaction = ({ transaction }: TransactionProps) => {
  const category = categoryImages.find((category) => category.name === transaction.categoryName);

  return (
    <div className="flex w-full justify-between items-center">
      <div className="flex gap-3 pc:gap-5">
        <div className="flex justify-center items-center w-12 h-12 bg-neutral-200 rounded-full">
          {category && <category.Icon />}
        </div>
        <div className="flex flex-col justify-center max-w-[150px] pc:max-w-[300px]">
          <div className="truncate text-sm pc:text-md">{transaction.merchantName}</div>
          <div className="text-neutral-400 text-xs">{transaction.paymentType}</div>
        </div>
      </div>
      <div className="text-sm pc:text-md">
        {transaction.transactionType === "SPEND" ? "-" : ""}
        {transaction.transactionBalance.toLocaleString()}원
      </div>
    </div>
  );
};

export default Transaction;
