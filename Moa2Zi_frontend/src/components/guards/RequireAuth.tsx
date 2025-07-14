import { ReactNode, useEffect, useState } from "react";
import { Navigate, useLocation } from "react-router-dom";
import { useAuthStore } from "@/stores/useAuthStore";
import { queryClient } from "@/lib/queryClient";
import apiClient from "@/services/http";

const RequireAuth = ({ children }: { children: ReactNode }) => {
  const { isLoggedIn, setIsLoggedIn } = useAuthStore();
  const location = useLocation();
  const [checkingAuth, setCheckingAuth] = useState<boolean>(true);

  useEffect(() => {
    const checkToken = async () => {
      const token = queryClient.getQueryData(["accessToken"]);

      if (!token) {
        try {
          const res = await apiClient.post("/reissue", {}, { withCredentials: true });
          const newToken = res.headers.access;

          if (newToken) {
            queryClient.setQueryData(["accessToken"], newToken);
            setIsLoggedIn(true);
          } else {
            setIsLoggedIn(false);
          }
        } catch {
          setIsLoggedIn(false);
        }
      } else {
        setIsLoggedIn(true);
      }

      setCheckingAuth(false);
    };

    checkToken();
  }, [setIsLoggedIn]);

  if (checkingAuth) {
    return null;
  }

  if (!isLoggedIn) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return <>{children}</>;
};

export default RequireAuth;
