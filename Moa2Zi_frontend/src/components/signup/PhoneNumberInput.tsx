import { ChangeEvent, Dispatch, SetStateAction } from "react";
import { AxiosError } from "axios";
import { toast, Toaster } from "sonner";
import CommonButton from "@/components/common/CommonButton";
import { postPhoneNumber } from "@/services/auth";

interface PhoneNumberInputProps {
  phoneNumber: string;
  setPhoneNumber: Dispatch<SetStateAction<string>>;
  setIsCounting: Dispatch<SetStateAction<boolean>>;
  setTimeLeft: Dispatch<SetStateAction<number>>;
}

const PhoneNumberInput = ({
  phoneNumber,
  setPhoneNumber,
  setIsCounting,
  setTimeLeft,
}: PhoneNumberInputProps) => {
  const formatPhoneNumber = (value: string) => {
    const onlyNumber = value.replace(/\D/g, "");
    if (onlyNumber.length < 4) return onlyNumber;
    if (onlyNumber.length < 8) {
      return `${onlyNumber.slice(0, 3)}-${onlyNumber.slice(3)}`;
    }
    return `${onlyNumber.slice(0, 3)}-${onlyNumber.slice(3, 7)}-${onlyNumber.slice(7, 11)}`;
  };

  const handleChangePhoneNumber = (e: ChangeEvent<HTMLInputElement>) => {
    setPhoneNumber(formatPhoneNumber(e.target.value));
  };

  const handleSubmitPhoneNumber = async () => {
    try {
      const response = await postPhoneNumber(phoneNumber.replace(/-/g, ""));
      // console.log(response);
      setTimeLeft(180);
      setIsCounting(true);
    } catch (error) {
      if ((error as AxiosError)?.response?.status === 409) {
        toast("이미 가입된 전화번호입니다.", {
          duration: 2000,
        });
      } else {
        // alert("인증 중 오류가 발생했습니다.");
      }
    }
  };

  return (
    <div className="flex flex-col w-full gap-5">
      <div className="flex flex-col gap-1">
        <div className="text-primary-500 font-bold text-lg">휴대폰 번호를 입력해주세요.</div>
        <div className="text-neutral-500 text-sm">본인 인증을 위해 필요합니다.</div>
      </div>
      <div className="flex flex-1 gap-3">
        <input
          className="flex-1 w-full border-b-2 border-neutral-300 outline-none focus:outline-none focus:ring-0 focus:border-primary-500 shadow-none"
          type="tel"
          placeholder="휴대폰 번호"
          pattern="[0-9]{3}-[0-9]{4}-[0-9]{4}"
          value={phoneNumber}
          onChange={handleChangePhoneNumber}
        />
        <div>
          <CommonButton
            variant="primary"
            className="w-28"
            onClick={handleSubmitPhoneNumber}
            disabled={phoneNumber.length < 10}
          >
            인증번호 받기
          </CommonButton>
          <Toaster position="top-center" toastOptions={{ className: "custom-toast-negative" }} />
        </div>
      </div>
    </div>
  );
};

export default PhoneNumberInput;
