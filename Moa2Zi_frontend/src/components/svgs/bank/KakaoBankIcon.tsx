import { SVGProps } from "@/types/svg";

const KakaoBankIcon = ({ className }: SVGProps) => {
  return (
    <svg
      className={className}
      width="80"
      height="80"
      viewBox="0 0 80 80"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
    >
      <rect width="80" height="80" rx="20" fill="#FFE300" />
      <path
        className={className}
        d="M41.6198 48.6474H38.3617V31.3495H41.6198V48.6474ZM50.1364 39.9986C52.9461 38.1466 54.8006 34.9664 54.8006 31.3498C54.8006 25.6335 50.1648 21 44.4485 21H27.0992C26.7666 21 26.5 21.2666 26.5 21.5992V58.3984C26.5 58.731 26.7666 59 27.0992 59H44.4485C50.1648 59 54.8006 54.3665 54.8006 48.648C54.8006 45.0314 52.9463 41.8482 50.1364 39.9986Z"
        fill="#1C1C1C"
      />
    </svg>
  );
};

export default KakaoBankIcon;
