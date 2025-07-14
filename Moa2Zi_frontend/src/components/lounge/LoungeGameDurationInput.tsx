import { Input } from "@/components/ui/input";

interface LoungeGameDurationInputProps {
  duration: string;
  setDuration: (value: string) => void;
  endDate: string;
}

const LoungeGameDurationInput = ({
  duration,
  setDuration,
  endDate,
}: LoungeGameDurationInputProps) => {
  const getRemainingDays = () => {
    const today = new Date();
    const end = new Date(endDate);
    const diffTime = end.getTime() - today.getTime();
    return Math.floor(diffTime / (1000 * 60 * 60 * 24));
  };

  const maxDuration = getRemainingDays();
  const isExceeded = Number(duration) > maxDuration;

  return (
    <div className="flex flex-col w-full gap-3 px-5">
      <div className="flex justify-between items-center">
        <div className="px-2 text-primary-500 font-semibold">라운쥐 게임 생성 주기</div>
        {typeof maxDuration === "number" && !isNaN(maxDuration) && (
          <span className={`text-xxs px-2 ${isExceeded ? "text-red-500" : "text-neutral-400"}`}>
            ※ {maxDuration}일 이내로 설정해주세요.
          </span>
        )}
      </div>
      <Input
        className="border-neutral-300 text-sm text-neutral-500 focus:border-primary-500 focus-visible:ring-primary-500"
        type="number"
        value={duration}
        onChange={(e) => setDuration(e.target.value)}
        min={1}
        max={maxDuration > 0 ? maxDuration : 1}
      />
    </div>
  );
};

export default LoungeGameDurationInput;
