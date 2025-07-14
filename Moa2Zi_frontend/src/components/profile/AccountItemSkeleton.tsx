import { Skeleton } from "@components/ui/skeleton";

const AccountItemSkeleton = () => {
  return (
    <div className="flex w-full px-5 gap-2">
      <Skeleton className="w-12 h-12 rounded-full" />
      <div className="flex flex-col justify-center gap-2">
        <Skeleton className="w-20 h-4" />
        <Skeleton className="w-40 h-3" />
      </div>
    </div>
  );
};

export default AccountItemSkeleton;
