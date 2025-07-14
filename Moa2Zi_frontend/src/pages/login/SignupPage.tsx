import { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { Toaster, toast } from "sonner";
import ProfleModal from "@/components/signup/ProfileModal";
import EmailInput from "@/components/signup/EmailInput";
import PassWordInput from "@/components/signup/PassWordInput";
import NicknameInput from "@/components/signup/NicknameInput";
import BirthInput from "@/components/signup/BirthInput";
import GenderInput from "@/components/signup/GenderInput";
import CommonButton from "@/components/common/CommonButton";
import { EmailCheckStatus } from "@/constants/emailCheckStatus";
import { PasswordCheckStatus } from "@/constants/passwordCheckStatus";
import { NicknameCheckStatus } from "@/constants/nicknameCheckStatus";
import { postSignup } from "@/services/auth";
import { SignupRequest } from "@/types/auths";
import { useVerification } from "@/hooks/useVerification";

const SignupPage = () => {
  const location = useLocation();
  const phoneNumber = location.state?.phoneNumber || "";

  const [profileImage, setProfileImage] = useState<string>("MouseIcon");
  const [email, setEmail] = useState<string>("");
  const [isEmailChecked, setIsEmailChecked] = useState<EmailCheckStatus>(
    EmailCheckStatus.Unchecked,
  );

  const [password, setPassword] = useState<string>("");
  const [passwordCheck, setPasswordCheck] = useState<string>("");
  const [isPasswordChecked, setIsPasswordChecked] = useState<PasswordCheckStatus>(
    PasswordCheckStatus.Unchecked,
  );

  const [nickname, setNickname] = useState<string>("");
  const [isNicknameChecked, setIsNicknameChecked] = useState<NicknameCheckStatus>(
    NicknameCheckStatus.Unchecked,
  );

  const [birth, setBirth] = useState<string>("");
  const [gender, setGender] = useState<string>("MALE");

  const navigate = useNavigate();
  const { getVerified } = useVerification();

  const handleSignup = async () => {
    try {
      const requestData: SignupRequest = {
        username: email,
        password: password,
        nickname: nickname,
        birthday: new Date(birth).toISOString(),
        gender: gender.toUpperCase() as "MALE" | "FEMALE",
        profileImage: `${profileImage}`,
        phoneNumber: phoneNumber.replace(/-/g, ""),

        memberTermList: [
          { termId: 1, agree: true },
          { termId: 2, agree: true },
          { termId: 3, agree: true },
          { termId: 4, agree: true },
          { termId: 5, agree: true },
          { termId: 6, agree: true },
        ],
      };

      const response = await postSignup(requestData);
      // console.log("회원가입 성공:", response.data);
      toast("회원가입에 성공하였습니다.", {
        duration: 1000,
      });
      setTimeout(() => {
        navigate("/login");
      }, 1000);
    } catch (error) {
      // console.error("회원가입 실패:", error);
      alert("회원가입 중 문제가 발생했습니다. 다시 시도해주세요.");
    }
  };

  useEffect(() => {
    if (!getVerified()) {
      navigate("/verification");
    }
  }, []);

  return (
    <div className="flex flex-col h-full min-h-screen w-full py-10 justify-center items-center px-6 pc:px-16 gap-3">
      <div className="text-xl font-medium">저희 가족이 되신걸 환영해요</div>
      <ProfleModal profileImage={profileImage} setProfileImage={setProfileImage} />
      <EmailInput
        email={email}
        setEmail={setEmail}
        isEmailChecked={isEmailChecked}
        setIsEmailChecked={setIsEmailChecked}
      />
      <PassWordInput
        password={password}
        setPassword={setPassword}
        passwordCheck={passwordCheck}
        setPasswordCheck={setPasswordCheck}
        status={isPasswordChecked}
        setStatus={setIsPasswordChecked}
      />
      <NicknameInput
        nickname={nickname}
        setNickname={setNickname}
        isNicknameChecked={isNicknameChecked}
        setIsNicknameChecked={setIsNicknameChecked}
      />
      <BirthInput birth={birth} setBirth={setBirth} />
      <GenderInput gender={gender} setGender={setGender} />
      <CommonButton
        variant="primary"
        className="w-full mt-3"
        onClick={handleSignup}
        disabled={
          isEmailChecked !== EmailCheckStatus.Valid ||
          isPasswordChecked !== PasswordCheckStatus.Valid ||
          isNicknameChecked !== NicknameCheckStatus.Valid ||
          !birth ||
          !gender
        }
      >
        이메일로 회원가입
      </CommonButton>
      <Toaster position="top-center" toastOptions={{ className: "custom-toast-positive" }} />
    </div>
  );
};

export default SignupPage;
