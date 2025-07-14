import { Link } from "react-router-dom";
import { Toaster } from "sonner";
import SettingIcon from "@/components/svgs/SettingIcon";
import MapAlertBar from "@components/expenseMap/MapAlertBar";
import { useGeoAlert } from "@/hooks/useGeoAlert";
import { useTestAlert } from "@/hooks/useExpenseAlert";

const Header = () => {
  // const { showAlert, alertContent } = useGeoAlert();

  // useTestAlert(true);

  return (
    <>
      <Toaster
        position="top-center"
        richColors
        toastOptions={{ className: "custom-toast-negative" }}
      />
      {/* {showAlert && <MapAlertBar content={alertContent} />} */}
      <header className="fixed top-0 left-0 right-0 w-full max-w-[600px] h-14 mx-auto bg-white pc:border-x border-b border-neutral-200 z-50">
        <nav className="h-full mx-auto">
          <ul className="flex justify-between items-center w-full h-full px-5">
            <li className="absolute left-1/2 transform -translate-x-1/2 flex items-center">
              <Link to={"/account-book/calendar"}>
                <img className="w-10" src="/logo.png" alt="로고" />
              </Link>
            </li>
            <li className="flex items-center ml-auto cursor-pointer">
              <Link to={"/setting"}>
                <SettingIcon />
              </Link>
            </li>
          </ul>
        </nav>
      </header>
    </>
  );
};

export default Header;
