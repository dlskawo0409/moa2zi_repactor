import { Outlet } from "react-router-dom";
import NavBar from "@/components/common/NavBar";

const ChallengePage = () => {
  return (
    <div className="flex flex-col">
      <NavBar />
      <Outlet />
    </div>
  );
};

export default ChallengePage;
