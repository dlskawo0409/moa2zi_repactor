import { useEffect } from "react";
import { reissueToken } from "@/lib/reissueToken";

const PublicRoute = ({ children }: { children: JSX.Element }) => {
  useEffect(() => {
    reissueToken({ redirectOnSuccess: true, redirectOnFail: false });
  }, []);

  return children;
};

export default PublicRoute;
