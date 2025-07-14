import { useState } from "react";
import { useParams } from "react-router-dom";
import { AnimatePresence } from "framer-motion";
import EmotionSelector from "@components/calendar/EmotionSelector";
import EmotionPlusIcon from "@/components/svgs/calendar/EmotionPlusIcon";
import CommonModal from "@components/common/CommonModal";
import TransactionItemModal from "@/components/calendar/TransactionItemModal";
import { TransactionList, EMOTION } from "@/types/calendar";
import EmotionIconBubble from "@/components/calendar/EmotionIconBubble";
import { useUserInfo } from "@/hooks/useUserInfo";
import ProfileFillIcon from "@components/svgs/ProfileFillIcon";

interface TransactionItemProps {
  index: number;
  item: TransactionList;
  isOpen: number | null;
  onToggleOpen: () => void;
  onClose: () => void;
  onEmotionSelect: (emotion: EMOTION, transactionId: number) => void;
}

const TransactionItem = ({
  index,
  item,
  isOpen,
  onToggleOpen,
  // onClose,
  onEmotionSelect,
}: TransactionItemProps) => {
  const { memberId } = useParams();
  const { data } = useUserInfo();
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [emotionOpen, setEmotionOpen] = useState<boolean>(false);

  const handleBubbleClick = (): void => {
    onToggleOpen();
    setEmotionOpen((prev) => !prev);
  };

  const handleEmotionSelect = (emotion: EMOTION, transactionId: number): void => {
    onEmotionSelect(emotion, transactionId);
    setEmotionOpen((prev) => !prev);
  }

  return (
    <div className="relative flex flex-col border-b border-neutral-300 py-5">
      <div className="flex justify-between">
        <div className="flex gap-3 pc:gap-5">
          {item.emotion ? (
            <EmotionIconBubble emotion={item.emotion} onSelect={handleBubbleClick} />
          ) : memberId == data?.memberId ? (
            <div
              className="flex justify-center items-center bg-neutral-100 w-12 h-12 rounded-full cursor-pointer"
              onClick={handleBubbleClick}
            >
              <EmotionPlusIcon  />
            </div>
          ) : (
            <div
              className="flex justify-center items-center bg-neutral-100 w-12 h-12 rounded-full"
            >
              <ProfileFillIcon className="size-7"/>
            </div>
          )}

          <div
            className={`flex flex-col ${memberId == data?.memberId ? "justify-between cursor-pointer" : "justify-center"}  py-0.5 pc:py-0`}
          >
            <CommonModal
              open={memberId == data?.memberId && isModalOpen}
              setOpen={setIsModalOpen}
              className="w-72 pc:w-112 rounded-xl"
              trigger={
                <div className="flex flex-col items-start gap-2">
                  <div className="flex font-medium text-sm pc:text-md">
                    {memberId == data?.memberId ? (
                      item.merchantName
                    ) : (
                      <div className="flex">
                        {item?.categoryName}
                        {item?.categoryName && item.subCategory?.subCategoryName && " > "}
                        {item.subCategory?.subCategoryName}
                      </div>
                    )}
                  </div>
                  {memberId == data?.memberId && (
                    <div className="flex gap-1 text-xs pc:text-sm text-neutral-600">
                      <div>
                        {item?.categoryName}
                        {item?.categoryName && item.subCategory?.subCategoryName && " > "}
                        {item.subCategory?.subCategoryName}
                      </div>
                      <div>
                        {(item?.categoryName || item?.subCategory.subCategoryName) && "|"}{" "}
                        {item.paymentType}
                      </div>
                    </div>
                  )}
                </div>
              }
            >
              <TransactionItemModal transaction={item} />
            </CommonModal>
          </div>
        </div>
        <div className="flex items-center text-sm pc:text-md text-negative-500 font-medium">
          - {item.transactionBalance.toLocaleString()}원
        </div>
      </div>

      <AnimatePresence>
        {emotionOpen && (
          <EmotionSelector
            key={item.transactionId}
            onSelect={handleEmotionSelect}
            onClose={setEmotionOpen}
            transactionId={item.transactionId}
          />
        )}
      </AnimatePresence>

    </div>
  );
};

export default TransactionItem;
