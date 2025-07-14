import { Skeleton } from "@/components/ui/skeleton";
import { useUserInfo } from "@/hooks/useUserInfo";
import { getProfileIcon } from "@/utils/getProfileIcon";

const FriendUserInfo = () => {
  const { data } = useUserInfo();
  const Icon = data?.profileImage ? getProfileIcon(data.profileImage) : null;

  return (
    <div>
      {!data ? (
        <div className="flex flex-col w-36 gap-2 mb-1">
          <Skeleton className="w-12 h-12" />
          <Skeleton className="w-20 h-4" />
        </div>
      ) : (
        <>
          <div className="flex flex-col min-w-36 gap-1">
            <div className="w-12 h-12">{Icon && <Icon className="w-full h-full" />}</div>
            <div className="flex font-bold">{data.nickname}</div>
          </div>
        </>
      )}
    </div>
  );
};

export default FriendUserInfo;
