import { Skeleton } from "@/components/ui/skeleton";

const FriendItemSkeleton = () => {
  return (
    <div className="flex mx-5 justify-between">
      <div className="flex gap-4">
        <Skeleton className="w-12 h-12"></Skeleton>
        <div className="flex items-center">
          <Skeleton className="flex w-24 h-6"></Skeleton>
        </div>
      </div>
    </div>
  );
};

export default FriendItemSkeleton;
