import { ReactNode } from "react";

interface BankItemProps {
  icon: ReactNode;
  name: string;
  isSelected: boolean;
  onClick: () => void;
}

const BankItem = ({ icon, name, isSelected, onClick }: BankItemProps) => (
  <div
    className={`flex flex-col w-[30%] p-2 items-center justify-center border rounded-lg box-border cursor-pointer ${isSelected ? "border-primary-500 bg-primary-100" : "border-white bg-white"}`}
    onClick={onClick}
  >
    <div className="flex items-center justify-center w-[65%]">{icon}</div>
    <div className="flex justify-center text-sm text-neutral-500 pc:text-md">{name}</div>
  </div>
);

export default BankItem;
