import { useState, useEffect } from "react";

const VerificationHelp = () => {
  const [showContent, setShowContent] = useState<boolean>(false);

  useEffect(() => {
    const timer = setTimeout(() => {
      setShowContent(true);
    }, 1000);

    return () => clearTimeout(timer);
  }, []);

  return (
    <div className="relative flex justify-center my-5">
      <div className="flex justify-center w-8/12 h-40 border-4 bg-neutral-100 rounded-t-lg">
        <div className="m-2 w-full h-full bg-white rounded-t-lg">
          <div className="flex justify-center text-neutral-900">---</div>
          <div className="flex justify-center text-sm text-neutral-500">거래 내역</div>
          <div className="flex justify-between">
            <div className="flex flex-col p-3 text-xxs">
              <div>입금자명</div>
              <div className="flex font-bold">모앗쥐 1234</div>
            </div>
            <div className="flex flex-col p-3 text-xxs">
              <div>입금</div>
              <div className="flex font-bold">1원</div>
            </div>
          </div>
          <div className="flex justify-between">
            <div className="flex flex-col p-3 text-xxs">
              <div>입금자명</div>
              <div className="flex font-bold">모앗쥐 1234</div>
            </div>
            <div className="flex flex-col p-3 text-xxs">
              <div>입금</div>
              <div className="flex font-bold">1원</div>
            </div>
          </div>
        </div>
        <div
          className={`absolute flex justify-between px-6 py-3 bottom-[10%] w-[80%] border border-gray-300 bg-white rounded-lg transition-opacity duration-1000 ${
            showContent ? "opacity-100" : "opacity-0"
          }`}
        >
          <div className="flex flex-col">
            <div>입금자명</div>
            <div className="flex gap-2">
              <div className="flex justify-center font-bold text-xl">모앗쥐</div>
              <div className="flex justify-center font-bold border-2 border-dashed border-negative-500 text-xl">
                1234
              </div>
            </div>
          </div>
          <div className="flex flex-col">
            <div>입금</div>
            <div className="flex font-bold text-xl">1원</div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default VerificationHelp;
