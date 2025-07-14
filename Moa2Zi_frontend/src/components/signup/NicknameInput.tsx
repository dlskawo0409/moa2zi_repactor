import { ChangeEvent, Dispatch, SetStateAction } from "react";
import { postNicknameCheck } from "@/services/auth";
import { Input } from "@components/ui/input";
import CommonButton from "@components/common/CommonButton";
import { NicknameCheckStatus } from "@/constants/nicknameCheckStatus";
import { nicknameRegex } from "@/utils/regex";

interface NicknameInputProps {
  nickname: string;
  setNickname: Dispatch<SetStateAction<string>>;
  isNicknameChecked: NicknameCheckStatus;
  setIsNicknameChecked: Dispatch<SetStateAction<NicknameCheckStatus>>;
}
const NicknameInput = ({
  nickname,
  setNickname,
  isNicknameChecked,
  setIsNicknameChecked,
}: NicknameInputProps) => {
  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    setNickname(e.target.value);
    setIsNicknameChecked(NicknameCheckStatus.Unchecked);
  };

  const handleCheckNickname = async () => {
    if (!nicknameRegex.test(nickname)) {
      setIsNicknameChecked(NicknameCheckStatus.Invalid);
      return;
    }

    try {
      const response = await postNicknameCheck(nickname);
      // console.log(response);
      setIsNicknameChecked(NicknameCheckStatus.Valid);
    } catch (error) {
      setIsNicknameChecked(NicknameCheckStatus.InUse);
    }
  };

  const renderMessage = () => {
    switch (isNicknameChecked) {
      case NicknameCheckStatus.Invalid:
        return (
          <p className="text-sm text-negative-500 px-2">
            2~10자의 한글, 영문, 숫자만 사용할 수 있어요.
          </p>
        );
      case NicknameCheckStatus.InUse:
        return <p className="text-sm text-negative-500">이미 사용 중인 닉네임입니다.</p>;
      case NicknameCheckStatus.Valid:
        return <p className="text-sm text-green-600">사용 가능한 닉네임입니다.</p>;
      default:
        return null;
    }
  };

  return (
    <div className="flex flex-col w-full gap-2">
      <div className="flex">
        <div className="px-2 text-primary-500 font-semibold">닉네임</div>
        <div className="flex text-sm items-center text-negative-500">{renderMessage()}</div>
      </div>
      <div className="flex gap-3">
        <Input
          className="border-neutral-300 text-sm focus:border-primary-500 focus-visible:ring-primary-500"
          type="text"
          placeholder="닉네임"
          value={nickname}
          onChange={handleChange}
        />
        <CommonButton variant="primary" onClick={handleCheckNickname}>
          중복 확인
        </CommonButton>
      </div>
    </div>
  );
};

export default NicknameInput;
