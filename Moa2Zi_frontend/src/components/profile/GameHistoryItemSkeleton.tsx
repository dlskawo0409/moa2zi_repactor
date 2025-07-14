import { Skeleton } from "@components/ui/skeleton";

const GameHistoryItemSkeleton = () => {
  return (
    <Skeleton className="flex items-center gap-4 w-full h-20 border-l-4 px-4 shadow-md rounded-none"></Skeleton>
  );
};

export default GameHistoryItemSkeleton;
