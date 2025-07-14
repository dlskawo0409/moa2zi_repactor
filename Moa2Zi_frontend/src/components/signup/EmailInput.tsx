import { ChangeEvent, Dispatch, SetStateAction } from "react";
import { AxiosError } from "axios";
import { postEmailCheck } from "@/services/auth";
import { emailRegex } from "@/utils/regex";
import { EmailCheckStatus } from "@/constants/emailCheckStatus";
import { Input } from "@/components/ui/input";
import CommonButton from "@/components/common/CommonButton";

interface EmailInputProps {
  email: string;
  setEmail: Dispatch<SetStateAction<string>>;
  isEmailChecked: EmailCheckStatus;
  setIsEmailChecked: Dispatch<SetStateAction<EmailCheckStatus>>;
}

const EmailInput = ({ email, setEmail, isEmailChecked, setIsEmailChecked }: EmailInputProps) => {
  const onChangeEmail = (e: ChangeEvent<HTMLInputElement>) => {
    setEmail(e.target.value);
    setIsEmailChecked(EmailCheckStatus.Unchecked);
  };

  const isValidEmail = () => {
    return emailRegex.test(email);
  };

  const handleCheckEmail = async () => {
    if (!isValidEmail()) {
      setIsEmailChecked(EmailCheckStatus.Invalid);
      return;
    }

    try {
      const response = await postEmailCheck(email);
      // console.log(response);
      setIsEmailChecked(EmailCheckStatus.Valid);
    } catch (error) {
      const axiosError = error as AxiosError;
      if (axiosError.response?.status === 409) {
        setIsEmailChecked(EmailCheckStatus.InUse);
      }
    }
  };

  const renderMessage = () => {
    switch (isEmailChecked) {
      case EmailCheckStatus.InUse:
        return <p className="text-sm text-negative-500">이미 가입된 이메일입니다.</p>;
      case EmailCheckStatus.Invalid:
        return <p className="text-sm text-negative-500">이메일 형식을 확인해주세요.</p>;
      case EmailCheckStatus.Valid:
        return <p className="text-sm text-green-600">사용 가능한 이메일입니다.</p>;
      default:
        return null;
    }
  };

  return (
    <div className="flex flex-col w-full gap-2">
      <div className="flex">
        <div className="px-2 text-primary-500 font-semibold">이메일</div>
        <div className="flex text-sm items-center text-negative-500">{renderMessage()}</div>
      </div>
      <div className="flex w-full gap-3">
        <Input
          className="flex-1 border-neutral-300 text-sm focus:border-primary-500 focus-visible:ring-primary-500"
          type="email"
          placeholder="이메일"
          value={email}
          onChange={onChangeEmail}
        />
        <div>
          <CommonButton variant="primary" onClick={handleCheckEmail} className="w-full">
            중복 확인
          </CommonButton>
        </div>
      </div>
    </div>
  );
};

export default EmailInput;
