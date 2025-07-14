import { useState } from "react";
import HereMouseBeforeIcon from "@/components/svgs/HereMouseBeforeIcon";
import HereMouseAfterIcon from "@/components/svgs/HereMouseAfterIcon";

type Props = {
  onClick: () => void;
};

const CurrentLocationBtn = ({ onClick }: Props) => {
  const [isHovered, setIsHovered] = useState<boolean>(false);
  const [isClicked, setIsClicked] = useState<boolean>(false);

  const handleClick = () => {
    onClick();
    setIsClicked(true);
  };

  return (
    <button
      className="absolute bottom-16 left-5 z-10 bg-white text-primary-500 border border-primary-500 shadow-xl w-12 h-12 rounded-full flex items-center justify-center hover:bg-primary-100 transition"
      onClick={handleClick}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      <div className="w-8 h-8 flex justify-center items-center">
        {isHovered || isClicked ? (
          <HereMouseAfterIcon className="w-full h-full" />
        ) : (
          <HereMouseBeforeIcon className="w-full h-full" />
        )}
      </div>
    </button>
  );
};

export default CurrentLocationBtn;
