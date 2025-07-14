const Wave2Pattern = ({ color = "rgba(255,255,255,0.2)", spacing = 40 }) => {
  return (
    <svg
      className="absolute top-0 left-0 w-full h-full"
      xmlns="http://www.w3.org/2000/svg"
      viewBox={`0 0 ${spacing * 2} ${spacing}`}
      preserveAspectRatio="none"
    >
      <defs>
        <pattern
          id="wave-pattern"
          patternUnits="userSpaceOnUse"
          width={spacing * 2}
          height={spacing}
        >
          <path
            d={`M 0 ${spacing / 2} Q ${spacing / 2} 0, ${spacing} ${spacing / 2} T ${spacing * 2} ${spacing / 2}`}
            stroke={color}
            strokeWidth="2"
            fill="none"
          />
        </pattern>
      </defs>
      <rect width="100%" height="100%" fill="url(#wave-pattern)" />
    </svg>
  );
};

export default Wave2Pattern;
