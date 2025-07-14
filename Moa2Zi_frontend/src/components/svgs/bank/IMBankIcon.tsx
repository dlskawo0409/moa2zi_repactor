import { SVGProps } from "@/types/svg";

const IMBankIcon = ({ className }: SVGProps) => {
  return (
    <svg
      className={className}
      width="80"
      height="80"
      viewBox="0 0 80 80"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
    >
      <rect width="80" height="80" rx="20" fill="#00C4A6" />
      <path className={className} d="M23.2266 36.2266H14V52.9954H23.2266V36.2266Z" fill="#FFFFFF" />
      <mask
        id="mask0_46_2727"
        // style="mask-type:luminance"
        maskUnits="userSpaceOnUse"
        x="23"
        y="27"
        width="27"
        height="26"
      >
        <path
          className={className}
          d="M23.2266 36.2266C27.6725 36.2315 31.9349 37.9997 35.0786 41.1435C38.2223 44.2872 39.9906 48.5496 39.9954 52.9955H49.222C49.222 49.5817 48.5496 46.2014 47.2432 43.0474C45.9368 39.8935 44.022 37.0278 41.6081 34.6139C39.1942 32.2 36.3285 30.2852 33.1746 28.9788C30.0207 27.6724 26.6403 27 23.2266 27V36.2266Z"
          fill="#FFFFFF"
        />
      </mask>
      <g mask="url(#mask0_46_2727)">
        <path
          className={className}
          d="M49.222 27.002H23.2266V52.9974H49.222V27.002Z"
          fill="#FFFFFF"
        />
      </g>
      <path
        className={className}
        d="M65.9997 27V36.2266C61.5538 36.2315 57.2914 37.9997 54.1477 41.1435C51.004 44.2872 49.2357 48.5496 49.2309 52.9955H39.9951C39.9951 49.5809 40.6678 46.1998 41.9748 43.0453C43.2817 39.8908 45.1974 37.0247 47.6122 34.6107C50.0271 32.1966 52.8939 30.282 56.0489 28.9762C59.2039 27.6703 62.5852 26.9988 65.9997 27Z"
        fill="#FFFFFF"
      />
    </svg>
  );
};

export default IMBankIcon;
