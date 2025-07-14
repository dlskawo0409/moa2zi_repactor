import SurpriseMouseIcon from "@components/svgs/SurpriseMouseIcon";

const NotFoundPage = () => {
  return (
    <div className="flex justify-center items-center w-full h-screen">
      <div className="flex flex-col justify-center items-center gap-10">
        <div className="text-8xl font-bold">404</div>
        <SurpriseMouseIcon className="size-32" />
        <div className="text-center text-2xl font-semibold">
          <div>앗! 착각했쥐!</div>
          <div>이 경로가 아니쥐!</div>
        </div>
      </div>
    </div>
  );
};

export default NotFoundPage;
