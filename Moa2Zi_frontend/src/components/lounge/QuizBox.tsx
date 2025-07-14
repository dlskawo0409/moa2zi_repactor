import YesMouseIcon from "@components/svgs/lounge/YesMouseIcon";
import NoMouseIcon from "@components/svgs/lounge/NoMouseIcon";
import { Skeleton } from "@/components/ui/skeleton";
import { QuizAnswer } from "@/types/lounge";

interface QuizBoxProps {
  nowCount: number;
  context: string;
  targetStartDate: Date;
  targetEndDate: Date;
  onSubmit: (answer: QuizAnswer) => void;
  isLoading: boolean;
}

const QuizBox = ({
  nowCount,
  context,
  targetStartDate,
  targetEndDate,
  onSubmit,
  isLoading,
}: QuizBoxProps) => {
  if (isLoading) {
    return (
      <div className="flex flex-col items-center gap-14 mt-24 mb-3 w-full pc:w-96 pt-5 border-2 border-primary-400 rounded-lg">
        <Skeleton className="w-20 h-6" />
        <div className="flex flex-col items-center gap-3">
          <Skeleton className="w-40 h-6" />
          <Skeleton className="w-60 h-6" />
        </div>
        <div className="flex w-full justify-between bg-primary-400 py-5">
          <div className="flex w-1/2 justify-center items-center border-r border-primary-700">
            <Skeleton className="w-14 h-14" />
          </div>
          <div className="flex w-1/2 justify-center items-center border-l border-primary-700">
            <Skeleton className="w-14 h-14" />
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="flex flex-col items-center gap-14 mt-24 mb-3 w-full pc:w-96 pt-5 border-2 border-primary-400 rounded-lg">
      <div className="font-bold text-lg">퀴즈 NO.{nowCount}</div>
      <div className="flex flex-col items-center gap-3">
        <div className="flex gap-2">
          <div className="font-semibold">
            {targetStartDate.toLocaleDateString("ko-KR")} ~{" "}
            {targetEndDate.toLocaleDateString("ko-KR")}
          </div>
          <div>기간동안</div>
        </div>
        <div className="px-5 text-center break-keep">{context}</div>
      </div>
      <div className="flex w-full justify-between bg-primary-400 py-5">
        <div
          className="flex w-1/2 justify-center items-center border-r border-primary-700 cursor-pointer"
          onClick={() => onSubmit("YES")}
        >
          <YesMouseIcon />
        </div>
        <div
          className="flex w-1/2 justify-center items-center border-l border-primary-700 cursor-pointer"
          onClick={() => onSubmit("NO")}
        >
          <NoMouseIcon />
        </div>
      </div>
    </div>
  );
};

export default QuizBox;
