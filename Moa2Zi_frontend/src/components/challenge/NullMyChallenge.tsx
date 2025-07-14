import SurpriseMouseIcon from "@components/svgs/SurpriseMouseIcon";

const NullMyChallenge = () => {
  return (
    <div className="flex justify-center items-center w-full h-screen">
      <div className="flex flex-col justify-center items-center gap-10">
        <SurpriseMouseIcon className="size-32 " />
        <div className="text-center text-2xl font-semibold">
          <div>아직 참여한 챌린지가 없어요ㅠㅠ</div>
        </div>
      </div>
    </div>
  );
};

export default NullMyChallenge;
