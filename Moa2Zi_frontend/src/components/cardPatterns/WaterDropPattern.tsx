const WaterDropPattern = ({ color = "rgba(255,255,255,0.2)", size = 100 }) => {
  return (
    <svg
      className="absolute top-0 left-0 w-full h-full"
      xmlns="http://www.w3.org/2000/svg"
      viewBox={`0 0 ${size} ${size}`}
      preserveAspectRatio="xMidYMid slice"
    >
      <defs>
        <pattern id="water-drops" patternUnits="userSpaceOnUse" width={size} height={size}>
          <circle cx={size * 0.9} cy={size * 0.9} r={size * 0.6} fill={color} />
        </pattern>
      </defs>
      <rect width="100%" height="100%" fill="url(#water-drops)" />
    </svg>
  );
};

export default WaterDropPattern;
