import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import PhoneNumberInput from "@components/signup/PhoneNumberInput";
import VerificationInput from "@components/signup/VerificationInput";
import CommonButton from "@components/common/CommonButton";
import { useAgreement } from "@/hooks/useAgreement";
import { useVerification } from "@/hooks/useVerification";

const PhoneVerificationPage = () => {
  const [phoneNumber, setPhoneNumber] = useState<string>("");
  const [verificationNumber, setVerificationNumber] = useState<string>("");
  const [isCounting, setIsCounting] = useState<boolean>(false);
  const [timeLeft, setTimeLeft] = useState<number>(0);
  const [isVerified, setIsVerified] = useState<boolean>(false);

  const { setVerified } = useVerification();

  const navigate = useNavigate();
  const { getAgreement } = useAgreement();

  useEffect(() => {
    const agreed = getAgreement();
    if (!agreed) {
      navigate("/terms");
    }
  }, []);

  return (
    <div className="relative flex flex-col h-full min-h-screen w-full py-10 gap-36 items-center px-6 pc:px-16">
      <div className="flex flex-col justify-center mt-64 w-full gap-7 pc:gap-10">
        <PhoneNumberInput
          phoneNumber={phoneNumber}
          setPhoneNumber={setPhoneNumber}
          setIsCounting={setIsCounting}
          setTimeLeft={setTimeLeft}
        />
        <VerificationInput
          verificationNumber={verificationNumber}
          setVerificationNumber={setVerificationNumber}
          isCounting={isCounting}
          setIsCounting={setIsCounting}
          timeLeft={timeLeft}
          setTimeLeft={setTimeLeft}
          isVerified={isVerified}
          setIsVerified={setIsVerified}
          phoneNumber={phoneNumber}
        />
      </div>
      <div className="flex w-full">
        <CommonButton
          variant="primary"
          className="w-full"
          disabled={!isVerified}
          onClick={() => {
            setVerified(true);
            navigate("/signup", { state: { phoneNumber } });
          }}
        >
          다음
        </CommonButton>
      </div>
    </div>
  );
};

export default PhoneVerificationPage;
