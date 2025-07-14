import { useEffect, ChangeEvent, Dispatch, SetStateAction } from "react";
import CommonButton from "@/components/common/CommonButton";
import { postVerificationNumber } from "@/services/auth";

interface VerificationInputProps {
  verificationNumber: string;
  setVerificationNumber: Dispatch<SetStateAction<string>>;
  isCounting: boolean;
  setIsCounting: Dispatch<SetStateAction<boolean>>;
  timeLeft: number;
  setTimeLeft: Dispatch<SetStateAction<number>>;
  isVerified: boolean;
  setIsVerified: Dispatch<SetStateAction<boolean>>;
  phoneNumber: string;
}

const VerificationInput = ({
  verificationNumber,
  setVerificationNumber,
  isCounting,
  setIsCounting,
  timeLeft,
  setTimeLeft,
  isVerified,
  setIsVerified,
  phoneNumber,
}: VerificationInputProps) => {
  const handleChangeVerificationNumber = (e: ChangeEvent<HTMLInputElement>) => {
    setVerificationNumber(e.target.value);
  };

  useEffect(() => {
    let timer: NodeJS.Timeout;
    if (isCounting && timeLeft > 0) {
      timer = setTimeout(() => {
        setTimeLeft((prev) => prev - 1);
      }, 1000);
    } else if (timeLeft === 0 && isCounting) {
      setIsCounting(false);
    }
    return () => clearTimeout(timer);
  }, [timeLeft, isCounting]);

  const formatTime = (seconds: number) => {
    const m = String(Math.floor(seconds / 60)).padStart(2, "0");
    const s = String(seconds % 60).padStart(2, "0");
    return `${m}:${s}`;
  };

  const handleSubmitVerification = async () => {
    try {
      const response = await postVerificationNumber(
        phoneNumber.replace(/-/g, ""),
        verificationNumber,
      );
      // console.log(response);
      setIsVerified(true);
    } catch (error) {
      // console.log(error);
      // alert("인증 중 오류가 발생했습니다.");
    }
  };

  return (
    <div className="flex flex-col w-full gap-2">
      <div className="flex w-full gap-3">
        <div className="flex flex-col w-full gap-2 ">
          <div className="flex-1 w-full relative">
            <input
              className="flex-1 w-full pr-16 pb-2 border-b-2 border-neutral-300 outline-none focus:outline-none focus:ring-0 focus:border-primary-500 shadow-none"
              type="tel"
              placeholder="인증 번호"
              onChange={handleChangeVerificationNumber}
              value={verificationNumber}
            />
            {isCounting && (
              <span className="absolute top-1/2 right-2 -translate-y-1/2 text-sm text-negative-500">
                {formatTime(timeLeft)}
              </span>
            )}
          </div>
          {isVerified && <div className="text-positive-500">인증이 완료되었습니다.</div>}
        </div>
        <div>
          <CommonButton
            variant="primary"
            className="w-28"
            disabled={!isCounting}
            onClick={handleSubmitVerification}
          >
            인증하기
          </CommonButton>
        </div>
      </div>
    </div>
  );
};

export default VerificationInput;
