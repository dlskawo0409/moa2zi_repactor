import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Toaster, toast } from "sonner";
import { Input } from "@/components/ui/input";
import { LoginRequest } from "@/types/auths";
import { useLogin } from "@/hooks/useLogin";
import { useAuthStore } from "@/stores/useAuthStore";
import CommonButton from "@/components/common/CommonButton";
import CloseEyeIcon from "@/components/svgs/CloseEyeIcon";
import OpenEyeIcon from "@/components/svgs/OpenEyeIcon";
import KakaoIcon from "@/components/svgs/KakaoIcon";
import GoogleIcon from "@/components/svgs/GoogleIcon";
import NaverIcon from "@/components/svgs/NaverIcon";

const LoginPage = () => {
  const [email, setEmail] = useState<string>("");
  const [isPasswordVisible, setIsPasswordVisible] = useState<boolean>(false);
  const [password, setPassword] = useState<string>("");

  const navigate = useNavigate();
  const { mutate: login } = useLogin();

  const setIsLoggedIn = useAuthStore((state) => state.setIsLoggedIn);

  const handleLogin = () => {
    const requestData: LoginRequest = {
      username: email,
      password: password,
    };

    login(requestData, {
      onSuccess: () => {
        // console.log("로그인 성공");
        setIsLoggedIn(true);
        navigate("/account-book/calendar");
      },
      onError: (error) => {
        // console.error("로그인 실패:", error);
        toast("로그인에 실패하였습니다.", {
          duration: 3000,
        });
      },
    });
  };

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === "Enter") {
        handleLogin();
      }
    };

    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [email, password]);

  return (
    <div className="flex flex-col h-screen justify-center items-center px-6 pc:px-16 gap-5">
      <div className="text-xl font-medium">모앗쥐에 오신 것을 환영합니다!</div>
      <img className="w-28" src="/logo.png" alt="로고" />
      <Input
        className="border-primary-500 text-sm h-9 focus-visible:ring-primary-500"
        type="email"
        placeholder="이메일"
        onChange={(e) => setEmail(e.target.value)}
      />
      <div className="relative w-full">
        <Input
          className="border-primary-500 text-sm h-9 focus-visible:ring-primary-500"
          type={isPasswordVisible ? "text" : "password"}
          placeholder="비밀번호"
          onChange={(e) => setPassword(e.target.value)}
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
      <CommonButton variant="primary" className="w-full" onClick={handleLogin}>
        이메일로 로그인
      </CommonButton>
      <p
        className="text-xs text-neutral-500 font-medium underline cursor-pointer"
        onClick={() => navigate("/terms")}
      >
        아직 회원이 아니신가요?
      </p>
      <div className="flex gap-10 pc:gap-16 items-center cursor-pointer">
        {/* <KakaoIcon />
        <GoogleIcon />
        <NaverIcon /> */}
      </div>
      <Toaster position="top-center" toastOptions={{ className: "custom-toast-negative" }} />
    </div>
  );
};

export default LoginPage;
