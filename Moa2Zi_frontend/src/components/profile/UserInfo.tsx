import { Skeleton } from "@/components/ui/skeleton";
import { useUserInfo } from "@/hooks/useUserInfo";
import { getProfileIcon } from "@/utils/getProfileIcon";

const UserInfo = () => {
  const { data } = useUserInfo();
  const Icon = data?.profileImage ? getProfileIcon(data.profileImage) : null;

  return (
    <div className="flex justify-center">
      {!data ? (
        <div className="flex flex-col w-36 gap-3 mb-1 items-center">
          <Skeleton className="w-12 h-12" />
          <Skeleton className="w-20 h-5" />
        </div>
      ) : (
        <>
          <div className="flex flex-col gap-2 items-center">
            <div className="w-12 h-12">{Icon && <Icon className="w-full h-full" />}</div>
            <div className="flex font-bold text-center text-xl">{data.nickname}</div>
          </div>
        </>
      )}
    </div>
  );
};

export default UserInfo;
