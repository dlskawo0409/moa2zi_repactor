import { SVGProps } from "@/types/svg";

const KDBIcon = ({ className }: SVGProps) => {
  return (
    <svg
      className={className}
      width="80"
      height="80"
      viewBox="0 0 80 80"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
    >
      <rect width="80" height="80" rx="20" fill="#0A1B98" />
      <g clipPath="url(#clip0_46_1522)">
        <path
          className={className}
          d="M54.0359 47.8079C53.9417 46.2114 53.4369 44.6628 52.567 43.3025L38.5166 22H48.1485C49.7081 22 51.1547 22.9026 51.7237 23.7546L59.6891 35.8417C60.2944 36.7618 60.2944 38.3254 59.6891 39.2572L54.0359 47.8079Z"
          fill="white"
        />
        <path
          className={className}
          d="M38.6982 52.9409L47.4808 39.5359C47.7056 39.172 47.8626 38.7728 47.9448 38.3564L51.6027 43.9048C52.6498 45.4839 53.3378 48.5388 52.6639 49.886L51.8044 51.1882C51.0781 52.3034 49.7506 52.9428 48.1587 52.9428L38.6982 52.9409Z"
          fill="white"
        />
        <path
          className={className}
          d="M32.2504 58.0022C30.6908 58.0022 29.2462 57.0996 28.6772 56.2476L20.7098 44.1605C20.1045 43.2423 20.1045 41.6788 20.7098 40.7469L26.359 32.1963C26.4563 33.79 26.9617 35.3355 27.8299 36.6939L41.8823 58.0022H32.2504Z"
          fill="white"
        />
        <path
          className={className}
          d="M28.7955 36.0888C27.7504 34.5097 27.0604 31.4528 27.7343 30.1076L28.5958 28.8054C29.3201 27.6902 30.6497 27.0508 32.2396 27.0508H41.7L32.9195 40.4557C32.6959 40.8169 32.5382 41.2126 32.4534 41.6255L28.7955 36.0888Z"
          fill="white"
        />
      </g>
      <defs>
        <clipPath id="clip0_46_1522">
          <rect width="42" height="36" fill="white" transform="translate(19 22)" />
        </clipPath>
      </defs>
    </svg>
  );
};

export default KDBIcon;
