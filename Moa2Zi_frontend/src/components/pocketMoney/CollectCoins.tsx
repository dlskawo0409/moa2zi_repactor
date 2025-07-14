import { DotLottieReact } from "@lottiefiles/dotlottie-react";

const CollectCoins = () => {
  return (
    <div className="flex w-full relative justify-center items-center">
      <img className="w-36 h-36" src="/logo2.png" alt="" />
      <DotLottieReact
        src="https://lottie.host/05ccfcbc-abaa-4b87-b36b-6a71005161e2/oVKrmni98R.lottie"
        loop
        autoplay
        mode="reverse"
        className="absolute scale-x-150 scale-y-[200%]"
      />
    </div>
  );
};

export default CollectCoins;
