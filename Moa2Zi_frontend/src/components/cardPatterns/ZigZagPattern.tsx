const ZigZagPattern = ({ color = "rgba(255,255,255,0.2)", spacing = 20 }) => {
  return (
    <svg
      className="absolute top-0 left-0 w-full h-full"
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 100 100"
      preserveAspectRatio="none"
    >
      <defs>
        <pattern id="zigzag-pattern" patternUnits="userSpaceOnUse" width={spacing} height={spacing}>
          <path
            d={`M 0 ${spacing / 2} L ${spacing / 2} 0 L ${spacing} ${spacing / 2} L ${spacing * 1.5} 0`}
            stroke={color}
            strokeWidth="1"
            fill="none"
          />
        </pattern>
      </defs>

      <rect width="100%" height="100%" fill="url(#zigzag-pattern)" />
    </svg>
  );
};

export default ZigZagPattern;
