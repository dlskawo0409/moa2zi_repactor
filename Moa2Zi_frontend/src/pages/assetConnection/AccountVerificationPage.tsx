import { useState } from "react";
import { Toaster, toast } from "sonner";
import { ArrowLeft } from "lucide-react";

import CommonButton from "@components/common/CommonButton";
import VerificationHelp from "@/components/assetConnection/VerificationHelp";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuRadioGroup,
  DropdownMenuRadioItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Input } from "@/components/ui/input";
import { InputOTP, InputOTPGroup, InputOTPSlot } from "@/components/ui/input-otp";
import { bankList } from "@/constants/bankList";
import { Bank } from "@/types/bank";
import { postAccountAuth, postAccountAuthCheck } from "@/services/finance";

type AccountVerificationPageProps = {
  setPage: (page: number) => void;
};

const AccountVerificationPage = ({ setPage }: AccountVerificationPageProps) => {
  const [step, setStep] = useState<number>(1);
  const [selectedBank, setSelectedBank] = useState<Bank>(bankList[0]);
  const [accountNumber, setAccountNumber] = useState<string>("");
  const [verificationCode, setVerificationCode] = useState<string>("");
  const [accountNumberError, setAccountNumberError] = useState<boolean>(false);
  const [verificationCodeError, setVerificationCodeError] = useState<boolean>(false);

  const handleSendVerification = async () => {
    if (!accountNumber.trim()) {
      alert("계좌번호를 입력해주세요.");
      return;
    }

    try {
      await postAccountAuth(accountNumber);

      setStep(2);
    } catch (error) {
      setAccountNumberError(true);

      toast("계좌번호를 다시 확인해주세요", {
        duration: 3000,
      });

      setTimeout(() => setAccountNumberError(false), 2000);
    }
  };

  const handleVerificationSubmit = async () => {
    if (!verificationCode.trim()) {
      toast("인증번호를 입력해주세요", {
        duration: 3000,
      });

      setVerificationCodeError(true);
      setTimeout(() => setVerificationCodeError(false), 2000);

      return;
    }

    try {
      await postAccountAuthCheck(accountNumber, verificationCode);

      setPage(3);
    } catch (error) {
      toast("인증번호를 다시 확인해주세요", {
        duration: 3000,
      });

      setVerificationCodeError(true);
      setTimeout(() => setVerificationCodeError(false), 2000);

      // console.error("인증 실패:", error);
    }
  };

  return (
    <>
      <div className="sticky top-0 flex items-center w-full h-[55px] px-5 bg-white">
        <div className="cursor-pointer" onClick={() => setPage(1)}>
          <ArrowLeft />
        </div>
        <div className="absolute left-1/2 -translate-x-1/2 text-xl font-bold">쥐갑 연결</div>
      </div>

      <Toaster position="top-center" toastOptions={{ className: "custom-toast-negative" }} />

      {step === 1 ? (
        <>
          <div className="flex flex-col gap-5 p-5 overflow-auto">
            <div className="text-center">
              실명인증을 위해 보유하고 있는 <br />
              계좌정보를 입력해주세요
            </div>
            <div className="">
              <div>은행명</div>
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <div className="flex justify-between items-center p-2 border rounded-lg cursor-pointer">
                    <div>{selectedBank.bankName}</div>
                  </div>
                </DropdownMenuTrigger>
                <DropdownMenuContent className="max-h-60 overflow-auto">
                  <DropdownMenuRadioGroup
                    value={selectedBank.bankName}
                    onValueChange={(bankName) => {
                      const selected = bankList.find((bank) => bank.bankName === bankName);
                      if (selected) {
                        setSelectedBank(selected);
                      }
                    }}
                  >
                    {bankList.map((bank) => (
                      <DropdownMenuRadioItem key={bank.bankCode} value={bank.bankName}>
                        {bank.bankName}
                      </DropdownMenuRadioItem>
                    ))}
                  </DropdownMenuRadioGroup>
                </DropdownMenuContent>
              </DropdownMenu>
            </div>
            <div className="">
              <div>계좌번호</div>
              <Input
                className={`h-[42px] transition-all duration-300 ${
                  accountNumberError ? "border-negative-500 animate-shake" : ""
                }`}
                placeholder="'-' 없이 계좌번호 입력"
                type="number"
                value={accountNumber}
                onChange={(e) => {
                  const onlyNumbers = e.target.value.replace(/\D/g, "");
                  setAccountNumber(onlyNumbers);
                  setAccountNumberError(false);
                }}
                onKeyDown={(e) => {
                  if (["e", "E", "+", "-", "."].includes(e.key)) {
                    e.preventDefault();
                  }
                }}
              />
            </div>
          </div>

          <div className="px-5 mb-5 mt-auto">
            <CommonButton
              variant="primary"
              className="sticky bottom-0 left-0 w-full"
              onClick={handleSendVerification}
            >
              1원 송금 요청
            </CommonButton>
          </div>
        </>
      ) : (
        <>
          <div className="flex flex-col gap-5 mx-5 overflow-auto">
            <div className="text-center">
              <div>입력한 계좌로 1원을 보냈어요!</div>
              <div>입금내역에 표시된 숫자 4자리를 입력해주세요</div>
            </div>

            <VerificationHelp />

            <div className="flex justify-between mx-5 items-center gap-1">
              <div className="flex gap-2">
                <div className="text-neutral-500 w-6 h-6">
                  {<selectedBank.Icon className="w-full h-full" />}
                </div>
                <div className="text-neutral-500">{accountNumber} </div>
              </div>
              <div className="text-blue-500 underline" onClick={() => setStep(1)}>
                변경하기
              </div>
            </div>

            <div className="flex justify-center my-5">
              <InputOTP
                value={verificationCode}
                maxLength={4}
                onChange={(verificationCode) => {
                  setVerificationCode(verificationCode);
                  setVerificationCodeError(false);
                }}
              >
                <InputOTPGroup>
                  <InputOTPSlot
                    className={`transition-all duration-300 ${
                      verificationCodeError ? "border-negative-500 animate-shake" : ""
                    }`}
                    index={0}
                  />
                </InputOTPGroup>
                <InputOTPGroup>
                  <InputOTPSlot
                    className={`transition-all duration-300 ${
                      verificationCodeError ? "border-negative-500 animate-shake" : ""
                    }`}
                    index={1}
                  />
                </InputOTPGroup>
                <InputOTPGroup>
                  <InputOTPSlot
                    className={`transition-all duration-300 ${
                      verificationCodeError ? "border-negative-500 animate-shake" : ""
                    }`}
                    index={2}
                  />
                </InputOTPGroup>
                <InputOTPGroup>
                  <InputOTPSlot
                    className={`transition-all duration-300 ${
                      verificationCodeError ? "border-negative-500 animate-shake" : ""
                    }`}
                    index={3}
                  />
                </InputOTPGroup>
              </InputOTP>
            </div>
          </div>

          <div className="px-5 mb-5 mt-auto">
            <CommonButton
              variant="primary"
              className="sticky bottom-0 left-0 w-full"
              onClick={handleVerificationSubmit}
            >
              인증 완료
            </CommonButton>
          </div>
        </>
      )}
    </>
  );
};

export default AccountVerificationPage;
