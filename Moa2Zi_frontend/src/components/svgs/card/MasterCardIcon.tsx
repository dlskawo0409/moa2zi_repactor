import { SVGProps } from "@/types/svg";

const MasterCardIcon = ({ className }: SVGProps) => {
  return (
    <svg
      className={className}
      width="55"
      height="34"
      viewBox="0 0 55 34"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
    >
      <g opacity="0.7" clipPath="url(#clip0_1537_9755)">
        <path
          className={className}
          d="M20.0458 3.60938H34.8996V30.1454H20.0458V3.60938Z"
          fill="#FF6610"
        />
        <path
          className={className}
          d="M20.9891 16.8781C20.9891 11.4865 23.5353 6.70432 27.4491 3.60994C24.5729 1.35957 20.942 0 16.981 0C7.59684 0 0.00500488 7.54813 0.00500488 16.8781C0.00500488 26.2078 7.59684 33.7561 16.9808 33.7561C20.9418 33.7561 24.5726 32.3965 27.4491 30.146C23.5353 27.0986 20.9891 22.2696 20.9891 16.8781V16.8781Z"
          fill="#FFBB0B"
        />
        <path
          className={className}
          d="M54.9404 16.8781C54.9404 26.2078 47.3486 33.7561 37.9646 33.7561C34.0036 33.7561 30.3728 32.3965 27.4963 30.146C31.4573 27.0518 33.9566 22.2696 33.9566 16.8781C33.9566 11.4865 31.4101 6.70432 27.4963 3.60994C30.3726 1.35957 34.0036 0 37.9646 0C47.3486 0 54.9404 7.59511 54.9404 16.8781Z"
          fill="#FF0000"
        />
      </g>
      <defs>
        <clipPath id="clip0_1537_9755">
          <rect width="55" height="34" fill="white" />
        </clipPath>
      </defs>
    </svg>
  );
};

export default MasterCardIcon;
