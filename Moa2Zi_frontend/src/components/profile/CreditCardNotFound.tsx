import { useNavigate } from "react-router-dom";
import { Plus } from "lucide-react";

const CreditCardNotFound = () => {
  const navigate = useNavigate();

  return (
    <div
      className="flex-col w-full rounded-lg p-5 aspect-[1.8] flex items-center justify-center border-2 border-dashed border-gray-300 text-gray-400 pc:my-10"
      onClick={() => navigate("/asset-connection")}
    >
      <Plus size={50} strokeWidth={1} />
      카드를 등록해보세요
    </div>
  );
};

export default CreditCardNotFound;
