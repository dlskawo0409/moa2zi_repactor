const HexagonPattern = ({ color = "rgba(255,255,255,0.2)", spacing = 30 }) => {
  return (
    <svg
      className="absolute top-0 left-0 w-full h-full"
      xmlns="http://www.w3.org/2000/svg"
      viewBox={`0 0 ${spacing * 2} ${spacing * 1.5}`}
      preserveAspectRatio="none"
    >
      <defs>
        <pattern
          id="hexagon-pattern"
          patternUnits="userSpaceOnUse"
          width={spacing * 2}
          height={spacing * 1.73}
          patternTransform={`translate(-${spacing / 2}, -${spacing / 2})`}
        >
          <path
            d={`M ${spacing} 0 
                 L ${spacing * 2} ${spacing * 0.5} 
                 L ${spacing * 2} ${spacing * 1.23} 
                 L ${spacing} ${spacing * 1.73} 
                 L 0 ${spacing * 1.23} 
                 L 0 ${spacing * 0.5} Z`}
            stroke={color}
            strokeWidth="2"
            fill="none"
          />
        </pattern>
      </defs>
      <rect width="100%" height="100%" fill="url(#hexagon-pattern)" />
    </svg>
  );
};

export default HexagonPattern;
