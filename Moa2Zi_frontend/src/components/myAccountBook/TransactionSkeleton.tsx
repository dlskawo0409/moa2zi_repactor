import { Skeleton } from "@/components/ui/skeleton";

const TransactionSkeleton = () => {
  return (
    <div className="flex w-full justify-between items-center">
      <div className="flex gap-5">
        <Skeleton className="flex justify-center items-center w-12 h-12 bg-neutral-200 rounded-full" />
        <div className="flex flex-col justify-center">
          <div className="flex justify-center items-center w-32 h-6">
            <Skeleton className="w-full h-4" />
          </div>
          <div className="flex justify-center items-center w-20 h-[18px]">
            <Skeleton className="w-full h-3" />
          </div>
        </div>
      </div>
      <div className="flex justify-center items-center w-24 h-6">
        <Skeleton className="w-full h-5" />
      </div>
    </div>
  );
};

export default TransactionSkeleton;
