import { useEffect, useState } from "react";
import { Input } from "@/components/ui/input";

interface LoungeEndDateInputProps {
  date: string;
  setDate: (value: string) => void;
}

const LoungeEndDateInput = ({ date, setDate }: LoungeEndDateInputProps) => {
  const [minDate, setMinDate] = useState<string>("");

  useEffect(() => {
    const today = new Date();
    today.setMonth(today.getMonth() + 1);
    const formatted = today.toISOString().split("T")[0];
    setMinDate(formatted);
  }, []);

  // Input에 보여줄 값만 YYYY-MM-DD로 변환
  const displayDate = date ? new Date(date).toISOString().split("T")[0] : "";

  return (
    <div className="flex flex-col w-full gap-3 px-5">
      <div className="px-2 text-primary-500 font-semibold">라운쥐 종료 날짜</div>
      <Input
        className="border-neutral-300 text-sm text-neutral-500 focus:border-primary-500 focus-visible:ring-primary-500"
        type="date"
        value={displayDate}
        min={minDate}
        onChange={(e) => {
          const selectedDate = new Date(e.target.value);
          const isoDate = selectedDate.toISOString();
          setDate(isoDate);
        }}
      />
    </div>
  );
};

export default LoungeEndDateInput;
