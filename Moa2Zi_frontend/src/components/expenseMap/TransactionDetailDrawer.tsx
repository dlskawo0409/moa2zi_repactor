import { ReactNode, useEffect, useRef, useState } from "react";
import { getTransactionMarkers } from "@/services/map";
import { emotionIcons } from "@/constants/emotionIcons";
import { TransactionItem } from "@/types/transaction";
import CommonDrawer from "@/components/common/CommonDrawer";
import HereMouseBeforeIcon from "@components/svgs/HereMouseBeforeIcon";
import HereMouseAfterIcon from "@components/svgs/HereMouseAfterIcon";
import ProfileFillIcon from "@components/svgs/ProfileFillIcon";

interface TransactionDetailDrawerProps {
  trigger: ReactNode;
  geohashCode: string;
  address?: string;
  keyword: string;
  categoryId: number | null;
  startDate: number | null;
  endDate: number | null;
}

const formatDate = (raw: string | number) => {
  const str = raw.toString();
  if (str.length !== 8) return ""; // 예외 처리

  const yyyy = str.slice(0, 4);
  const mm = str.slice(4, 6);
  const dd = str.slice(6, 8);
  return `${yyyy}-${mm}-${dd}`;
};

const TransactionDetailDrawer = ({
  trigger,
  geohashCode,
  keyword,
  categoryId,
  startDate,
  endDate,
  address,
}: TransactionDetailDrawerProps) => {
  const [transactions, setTransactions] = useState<TransactionItem[]>([]);
  const [next, setNext] = useState<number>(0);
  const [hasNext, setHasNext] = useState<boolean>(true);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const hasFetchedRef = useRef<boolean>(false);
  const scrollRef = useRef<HTMLDivElement>(null);

  const fetchTransactions = async () => {
    if (!hasNext || isLoading || hasFetchedRef.current) return;
    hasFetchedRef.current = true;
    setIsLoading(true);

    try {
      const res = await getTransactionMarkers({
        keyword,
        categoryId,
        startDate,
        endDate,
        geohashCode,
        next,
        size: 200000000,
      });

      setTransactions((prev) => [...prev, ...res.transactionList]);
      setNext(res.next);
      setHasNext(res.hasNext);
    } catch (error) {
      // console.error("거래 내역 불러오기 실패:", error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    setTransactions([]);
    setNext(0);
    setHasNext(true);
    hasFetchedRef.current = false;
    fetchTransactions();
  }, [geohashCode]);

  const handleScroll = () => {
    const container = scrollRef.current;
    if (!container) return;
    if (container.scrollTop + container.clientHeight >= container.scrollHeight - 20) {
      fetchTransactions();
    }
  };

  const getEmotionIcon = (emotion: string) => {
    const match = emotionIcons.find((e) => e.name === emotion);
    return match && <match.Component className="w-full h-full" />;
  };

  return (
    <CommonDrawer
      trigger={trigger}
      header={
        <div className="flex justify-between items-center font-semibold text-base text-gray-800 px-2 pt-4">
          <div className="font-medium truncate">{address ?? "주소 정보를 불러오는 중..."}</div>
          <div className="text-xs text-gray-600 whitespace-nowrap">
            거래 : <span className="text-primary-500 font-bold pr-1">{transactions.length}</span>건
          </div>
        </div>
      }
      footer={
        <div className="text-center w-full text-gray-400 text-sm">
          거래내역을 모두 불러왔습니다.
        </div>
      }
    >
      <div
        ref={scrollRef}
        onScroll={handleScroll}
        className="max-h-[60vh] overflow-y-auto px-4 pb-4"
      >
        {transactions.map((t, idx) => (
          <div
            key={`${t.transactionId}-${idx}`}
            className="flex justify-between items-start py-3 border-b last:border-none"
          >
            {t.emotion ? (
              <div className="w-12 h-12 rounded-full  flex items-center justify-center mr-3 shrink-0">
                {getEmotionIcon(t.emotion)}
              </div>
            ) : (
              <div className="w-12 h-12 rounded-full  flex items-center justify-center mr-3 shrink-0 bg-neutral-100">
                <ProfileFillIcon className="w-[65%] pl-[2px]" />
              </div>
            )}

            <div className="flex-1">
              <div className="text-sm font-medium text-gray-800">{t.merchantName}</div>
              <div className="text-xs text-gray-500">{t.paymentMethod}</div>
            </div>

            <div className="flex flex-col items-end text-right whitespace-nowrap pl-2">
              <div className="text-sm font-semibold text-red-500">
                -{t.transactionBalance.toLocaleString()}원
              </div>
              <div className="text-[11px] text-gray-400 mt-1">
                결제일: {formatDate(t.transactionDate)}
              </div>
            </div>
          </div>
        ))}
      </div>
    </CommonDrawer>
  );
};

export default TransactionDetailDrawer;
