import { useState, Dispatch, SetStateAction } from "react";
import { Input } from "@/components/ui/input";
import CloseEyeIcon from "@/components/svgs/CloseEyeIcon";
import OpenEyeIcon from "@/components/svgs/OpenEyeIcon";
import { passwordRegex } from "@/utils/regex";
import { PasswordCheckStatus } from "@/constants/passwordCheckStatus";

interface PassWordInputProps {
  password: string;
  setPassword: Dispatch<SetStateAction<string>>;
  passwordCheck: string;
  setPasswordCheck: Dispatch<SetStateAction<string>>;
  status: PasswordCheckStatus;
  setStatus: Dispatch<SetStateAction<PasswordCheckStatus>>;
}

const PassWordInput = ({
  password,
  setPassword,
  passwordCheck,
  setPasswordCheck,
  status,
  setStatus,
}: PassWordInputProps) => {
  const [isPasswordVisible, setIsPasswordVisible] = useState<boolean>(false);
  const [isPasswordCheckVisible, setIsPasswordCheckVisible] = useState<boolean>(false);

  const validatePassword = (pw: string, check: string) => {
    if (!pw && !check) {
      setStatus(PasswordCheckStatus.Unchecked);
    } else if (!passwordRegex.test(pw)) {
      setStatus(PasswordCheckStatus.Invalid);
    } else if (pw !== check) {
      setStatus(PasswordCheckStatus.NotMatched);
    } else {
      setStatus(PasswordCheckStatus.Valid);
    }
  };

  const handlePasswordChange = (value: string) => {
    setPassword(value);
    validatePassword(value, passwordCheck);
  };

  const handlePasswordCheckChange = (value: string) => {
    setPasswordCheck(value);
    validatePassword(password, value);
  };

  const renderMessage = () => {
    if (password.length > 0 && passwordCheck.length > 0) {
      switch (status) {
        case PasswordCheckStatus.Invalid:
          return (
            <p className="text-sm text-negative-500">
              영문자 + 특수문자 포함 9자 이상이어야 합니다.
            </p>
          );
        case PasswordCheckStatus.NotMatched:
          return <p className="text-sm text-negative-500">비밀번호가 일치하지 않습니다.</p>;
        case PasswordCheckStatus.Valid:
          return <p className="text-sm text-green-600">사용 가능한 비밀번호입니다.</p>;
        default:
          return null;
      }
    }
  };

  return (
    <>
      <div className="flex flex-col w-full gap-2">
        <div className="flex">
          <div className="px-2 text-primary-500 font-semibold">비밀번호</div>
        </div>
        <div className="relative">
          <Input
            className="border-neutral-300 text-sm focus:border-primary-500 focus-visible:ring-primary-500"
            type={isPasswordVisible ? "text" : "password"}
            placeholder="비밀번호"
            value={password}
            onChange={(e) => handlePasswordChange(e.target.value)}
          />
          <div
            onClick={() => setIsPasswordVisible(!isPasswordVisible)}
            className="absolute top-2 right-4 cursor-pointer"
          >
            {isPasswordVisible ? (
              <OpenEyeIcon className="stroke-neutral-600" />
            ) : (
              <CloseEyeIcon className="stroke-neutral-600" />
            )}
          </div>
        </div>
      </div>

      <div className="flex flex-col w-full gap-2 mt-4">
        <div className="flex">
          <div className="px-2 text-primary-500 font-semibold">비밀번호 확인</div>
          <div className="flex text-sm items-center text-negative-500">{renderMessage()}</div>
        </div>
        <div className="relative">
          <Input
            className="border-neutral-300 text-sm focus:border-primary-500 focus-visible:ring-primary-500"
            type={isPasswordCheckVisible ? "text" : "password"}
            placeholder="비밀번호 확인"
            value={passwordCheck}
            onChange={(e) => handlePasswordCheckChange(e.target.value)}
          />
          <div
            onClick={() => setIsPasswordCheckVisible(!isPasswordCheckVisible)}
            className="absolute top-2 right-4 cursor-pointer"
          >
            {isPasswordCheckVisible ? (
              <OpenEyeIcon className="stroke-neutral-600" />
            ) : (
              <CloseEyeIcon className="stroke-neutral-600" />
            )}
          </div>
        </div>
      </div>
    </>
  );
};

export default PassWordInput;
