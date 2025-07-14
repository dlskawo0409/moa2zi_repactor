import { useNavigate } from "react-router-dom";
import { Plus } from "lucide-react";

const LoungeStartButton = () => {
  const navigate = useNavigate();

  return (
    <div className="fixed w-full flex justify-end bottom-0 max-w-[600px] bg-white z-40">
      <div className="relative">
        <div
          className="absolute bottom-20 right-10 flex justify-center items-center bg-primary-500 hover:bg-primary-400 text-white w-14 h-14 rounded-full text-sm font-bold transition-colors ease-in-out cursor-pointer"
          onClick={() => navigate("/lounge/start")}
        >
          <Plus />
        </div>
      </div>
    </div>
  );
};

export default LoungeStartButton;
