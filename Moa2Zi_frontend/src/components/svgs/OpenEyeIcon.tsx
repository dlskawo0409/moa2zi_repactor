import { SVGProps } from "@/types/svg";

const OpenEyeIcon = ({ className }: SVGProps) => {
  return (
    <svg
      className={className}
      width="20"
      height="20"
      viewBox="0 0 20 20"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
    >
      <path
        className={className}
        d="M18.3334 9.99996C18.3334 9.99996 15.8334 4.16663 10 4.16663C4.16671 4.16663 1.66671 9.99996 1.66671 9.99996C1.66671 9.99996 4.16671 15.8333 10 15.8333C15.8334 15.8333 18.3334 9.99996 18.3334 9.99996Z"
        stroke="#949494"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <path
        className={className}
        d="M10 12.5C8.61929 12.5 7.5 11.3807 7.5 10C7.5 8.61929 8.61929 7.5 10 7.5C11.3807 7.5 12.5 8.61929 12.5 10C12.5 11.3807 11.3807 12.5 10 12.5Z"
        stroke="#949494"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  );
};

export default OpenEyeIcon;
