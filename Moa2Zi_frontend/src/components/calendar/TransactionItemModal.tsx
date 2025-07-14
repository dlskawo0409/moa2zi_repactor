import { useParams } from "react-router-dom";
import CommonButton from "@components/common/CommonButton";
import { TransactionList } from "@/types/calendar";
import { formatNumToDate, formatTimeToKorean } from "@/utils/formatDate";

interface ModalContentProps {
  transaction: TransactionList;
}

const TransactionItemModal = ({ transaction }: ModalContentProps) => {
  const { transactionDate } = useParams();

  return (
    <div className="flex flex-col gap-3 pc:gap-6 p-3">
      <div className="flex flex-col gap-1">
        <div className="text-lg pc:text-xl">금액</div>
        <div className="text-xl pc:text-2xl font-semibold">
          {transaction.transactionBalance.toLocaleString()} 원
        </div>
      </div>

      <div className="flex gap-8">
        <CommonButton variant="primary" className="w-full h-9">
          수입
        </CommonButton>
        <CommonButton variant="neutral" className="w-full h-9">
          지출
        </CommonButton>
      </div>
      <div className="flex justify-between border-t border-neutral-200 pt-5 text-sm pc:text-md">
        <div>거래처</div>
        <div>{transaction.merchantName}</div>
      </div>
      <div className="flex justify-between border-t border-neutral-200 pt-5 text-sm pc:text-md">
        <div>카테고리</div>
        <div>{transaction.subCategory.subCategoryName}</div>
      </div>
      <div className="flex justify-between border-t border-neutral-200 pt-5 text-sm pc:text-md">
        <div>결제 수단</div>
        <div>{transaction.paymentType}</div>
      </div>
      <div className="flex justify-between border-t border-neutral-200 pt-5 text-sm pc:text-md">
        <div>날짜</div>
        <div>
          {formatNumToDate(transactionDate)} {formatTimeToKorean(transaction.transactionTime)}
        </div>
      </div>
      <div className="flex justify-between border-t border-neutral-200 pt-5 text-sm pc:text-md">
        <div>메모</div>
        {transaction.memo ? <div>{transaction.memo}</div> : <div>메모가 없습니다.</div>}
      </div>
    </div>
  );
};

export default TransactionItemModal;
