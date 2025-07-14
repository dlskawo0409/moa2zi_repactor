import { SVGProps } from "@/types/svg";

const CloseEyeIcon = ({ className }: SVGProps) => {
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
        d="M8.23335 8.23328C7.98772 8.46215 7.79072 8.73815 7.65408 9.04482C7.51744 9.35148 7.44396 9.68252 7.43804 10.0182C7.43212 10.3539 7.49387 10.6873 7.6196 10.9986C7.74534 11.3099 7.93249 11.5927 8.16989 11.8301C8.40728 12.0675 8.69006 12.2546 9.00136 12.3804C9.31265 12.5061 9.64608 12.5678 9.98176 12.5619C10.3174 12.556 10.6485 12.4825 10.9551 12.3459C11.2618 12.2092 11.5378 12.0122 11.7667 11.7666"
        stroke="#949494"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <path
        className={className}
        d="M8.94168 4.23329C9.29277 4.18955 9.64621 4.16729 10 4.16663C15.8333 4.16663 18.3333 9.99996 18.3333 9.99996C17.9608 10.7976 17.4935 11.5474 16.9417 12.2333"
        stroke="#949494"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <path
        className={className}
        d="M5.50832 5.5083C3.85103 6.63715 2.52488 8.18768 1.66666 9.99997C1.66666 9.99997 4.16666 15.8333 9.99999 15.8333C11.5966 15.8376 13.159 15.3709 14.4917 14.4916"
        stroke="#949494"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <path
        className={className}
        d="M1.66666 1.66663L18.3333 18.3333"
        stroke="#949494"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  );
};

export default CloseEyeIcon;
