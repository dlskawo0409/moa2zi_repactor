import { SVGProps } from "@/types/svg";

const ICChipIcon = ({ className }: SVGProps) => {
  return (
    <svg
      className={className}
      width="33"
      height="24"
      viewBox="0 0 33 24"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
    >
      <rect y="1" width="33" height="22" rx="5" fill="url(#paint0_radial_1427_9764)" />
      <path
        className={className}
        fillRule="evenodd"
        clipRule="evenodd"
        d="M16.5 1.21875L12.8333 6.96808C12.8333 6.96808 14.3 11.9988 12.8333 16.3108L16.5 22.7788L20.1667 16.3108C20.1667 16.3108 18.7 12.7174 20.1667 6.96808C20.1667 6.96808 16.5 1.21875 16.5 1.21875Z"
        stroke="#C7C7B6"
        strokeWidth="1.3"
        strokeLinejoin="bevel"
      />
      <path
        className={className}
        d="M0 7.2319H12.8333"
        stroke="#C7C7B6"
        strokeWidth="1.3"
        strokeLinejoin="bevel"
      />
      <path
        className={className}
        d="M19.7996 7.2319H32.9996"
        stroke="#C7C7B6"
        strokeWidth="1.3"
        strokeLinejoin="bevel"
      />
      <path
        className={className}
        d="M0 16.0327H12.8333"
        stroke="#C7C7B6"
        strokeWidth="1.3"
        strokeLinejoin="bevel"
      />
      <path
        d="M19.7996 16.0327H32.9996"
        stroke="#C7C7B6"
        strokeWidth="1.3"
        strokeLinejoin="bevel"
      />
      <defs>
        <radialGradient
          id="paint0_radial_1427_9764"
          cx="0"
          cy="0"
          r="1"
          gradientUnits="userSpaceOnUse"
          gradientTransform="translate(18.9503 9.01386) rotate(141.302) scale(22.37 22.2003)"
        >
          <stop stopColor="#FFF9CD" />
          <stop offset="1" stopColor="#FFE372" />
        </radialGradient>
      </defs>
    </svg>
  );
};

export default ICChipIcon;
