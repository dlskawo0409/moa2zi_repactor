import { useEffect, useState } from "react";
import { Input } from "@/components/ui/input";

interface BirthInputProps {
  birth: string;
  setBirth: (value: string) => void;
}

const BirthInput = ({ birth, setBirth }: BirthInputProps) => {
  const [today, setToday] = useState<string>("");

  useEffect(() => {
    const now = new Date();
    const yyyy = now.getFullYear();
    const mm = String(now.getMonth() + 1).padStart(2, "0");
    const dd = String(now.getDate()).padStart(2, "0");
    setToday(`${yyyy}-${mm}-${dd}`);
  }, []);

  return (
    <div className="flex flex-col w-full gap-2">
      <div className="px-2 text-primary-500 font-semibold">생년월일</div>
      <Input
        className="border-neutral-300 text-sm text-neutral-500 focus:border-primary-500 focus-visible:ring-primary-500"
        type="date"
        value={birth}
        onChange={(e) => setBirth(e.target.value)}
        max={today}
      />
    </div>
  );
};

export default BirthInput;
