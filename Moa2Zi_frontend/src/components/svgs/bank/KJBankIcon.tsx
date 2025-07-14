import { SVGProps } from "@/types/svg";

const KJBankIcon = ({ className }: SVGProps) => {
  return (
    <svg
      className={className}
      width="80"
      height="80"
      viewBox="0 0 80 80"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
    >
      <rect width="80" height="80" rx="20" fill="#0091D0" />
      <path className={className} d="M57 23H23V57H57V23Z" fill="#FFFFFF" />
      <path
        className={className}
        d="M56.9998 44.2523L46.1147 33.8848L29.5928 50.4426L35.6177 56.4747L46.1147 46.136L56.9998 56.9636V44.2523Z"
        fill="#0091D0"
      />
    </svg>
  );
};

export default KJBankIcon;
