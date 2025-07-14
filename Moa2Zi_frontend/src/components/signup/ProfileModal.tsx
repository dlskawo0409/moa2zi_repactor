import { useState, Dispatch, SetStateAction } from "react";
import CommonModal from "@/components/common/CommonModal";
import { profileImages } from "@/constants/profileImages";

interface ProfileModalProps {
  profileImage: string;
  setProfileImage: Dispatch<SetStateAction<string>>;
}

const ProfleModal = ({ profileImage, setProfileImage }: ProfileModalProps) => {
  const [isOpen, setIsOpen] = useState<boolean>(false);

  const handleImageSelect = (imageName: string) => {
    setProfileImage(imageName);
    setIsOpen(false);
  };

  return (
    <CommonModal
      open={isOpen}
      setOpen={setIsOpen}
      className="w-80 pc:w-96 rounded-xl"
      trigger={
        <div className="relative cursor-pointer">
          {(() => {
            const selected = profileImages.find((img) => img.name === profileImage);
            const SelectedComponent = selected?.Component;
            return SelectedComponent ? (
              <SelectedComponent className="size-24 rounded-full border-2 border-primary-500" />
            ) : null;
          })()}
          <div className="absolute bottom-1 right-1 w-5 h-5 pc:w-6 pc:h-6 border font-semibold bg-white border-black rounded-full flex justify-center items-center">
            +
          </div>
        </div>
      }
    >
      <div className="p-4">
        <p className="mb-4 font-semibold">프로필 사진을 선택해 주세요.</p>
        <div className="grid grid-cols-3 gap-2 max-h-[400px] overflow-y-auto scrollbar-hide">
          {profileImages.map(({ name, Component }) => (
            <div
              key={name}
              className="p-1 rounded cursor-pointer"
              onClick={() => handleImageSelect(name)}
            >
              <Component className="w-16 h-16 pc:w-20 pc:h-20 rounded-full border-2 hover:border-primary-500" />
            </div>
          ))}
        </div>
      </div>
    </CommonModal>
  );
};

export default ProfleModal;
