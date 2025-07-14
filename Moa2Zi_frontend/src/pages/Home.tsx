import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import CommonButton from "@components/common/CommonButton";
import { useAuthStore } from "@/stores/useAuthStore";

const Home = () => {
  const navigate = useNavigate();
  const isLoggedIn = useAuthStore((state) => state.isLoggedIn);

  useEffect(() => {
    if (isLoggedIn) {
      navigate("/account-book/calendar", { replace: true });
    }
  }, [isLoggedIn, navigate]);

  return (
    <div className="flex flex-col">
      <div className="text-primary-500">인트로 페이지</div>
      <CommonButton variant="primary" onClick={() => navigate("/login")}>
        로그인하러 가기
      </CommonButton>
    </div>
  );
};

export default Home;
