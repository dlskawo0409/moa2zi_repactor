import { SVGProps } from "@/types/svg";

const CircleXIcon = ({ className }: SVGProps) => {
  return (
    <svg
      className={className}
      width="23"
      height="24"
      viewBox="0 0 23 24"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
    >
      <path
        className={className}
        d="M5.65687 17.6568C7.15716 19.1571 9.19199 20 11.3137 20C13.4355 20 15.4703 19.1571 16.9706 17.6568C18.4709 16.1566 19.3137 14.1217 19.3137 12C19.3137 9.87826 18.4709 7.84342 16.9706 6.34313C15.4703 4.84284 13.4355 3.99999 11.3137 3.99999C9.19199 3.99999 7.15716 4.84284 5.65687 6.34313C4.15658 7.84342 3.31372 9.87826 3.31372 12C3.31372 14.1217 4.15658 16.1566 5.65687 17.6568Z"
        stroke="#949494"
        stroke-width="2"
        stroke-linecap="round"
        stroke-linejoin="round"
      />
      <path
        className={className}
        d="M13.4348 9.87866C13.4348 9.87866 10.849 12.4644 9.19217 14.1213"
        stroke="#949494"
        stroke-width="2"
        stroke-linecap="round"
        stroke-linejoin="round"
      />
      <path
        className={className}
        d="M9.19214 9.87769L13.4348 14.1203"
        stroke="#949494"
        stroke-width="2"
        stroke-linecap="round"
        stroke-linejoin="round"
      />
    </svg>
  );
};

export default CircleXIcon;
