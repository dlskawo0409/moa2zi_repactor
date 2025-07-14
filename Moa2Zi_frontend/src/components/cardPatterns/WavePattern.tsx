const WavePattern = ({ color = "rgba(255,255,255,0.2)" }) => {
  return (
    <svg
      className="absolute top-0 left-0 w-full h-full"
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 1440 320"
      preserveAspectRatio="none"
    >
      <path
        fill={color}
        d="M0,256L60,234.7C120,213,240,171,360,176C480,181,600,235,720,245.3C840,256,960,224,1080,208C1200,192,1320,192,1380,192L1440,192L1440,0L1380,0C1320,0,1200,0,1080,0C960,0,840,0,720,0C600,0,480,0,360,0C240,0,120,0,60,0L0,0Z"
      ></path>
    </svg>
  );
};

export default WavePattern;
