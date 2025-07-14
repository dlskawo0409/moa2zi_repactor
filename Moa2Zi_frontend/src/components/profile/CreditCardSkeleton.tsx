import { Skeleton } from "@components/ui/skeleton";

const CreditCardSkeleton = () => {
  return (
    <Skeleton
      className={`w-full rounded-lg p-5 aspect-[1.8] flex flex-col relative pc:my-10`}
    ></Skeleton>
  );
};

export default CreditCardSkeleton;
