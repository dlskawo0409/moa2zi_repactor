import { Dispatch, SetStateAction, ChangeEvent } from "react";
import { Input } from "@components/ui/input";

interface LoungeTitleInputProps {
  title: string;
  setTitle: Dispatch<SetStateAction<string>>;
}

const LoungeTitleInput = ({ title, setTitle }: LoungeTitleInputProps) => {
  const onChangeTitle = (e: ChangeEvent<HTMLInputElement>) => {
    setTitle(e.target.value);
  };

  return (
    <div className="flex flex-col w-full px-5 gap-3">
      <div className="flex">
        <div className="px-2 text-primary-500 font-semibold">라운쥐 이름</div>
      </div>
      <div className="flex w-full gap-3">
        <Input
          className="flex-1 border-neutral-300 text-sm focus:border-primary-500 focus-visible:ring-primary-500"
          type="email"
          placeholder="라운쥐 이름을 입력해주세요"
          value={title}
          maxLength={20}
          onChange={onChangeTitle}
        />
      </div>
    </div>
  );
};

export default LoungeTitleInput;
