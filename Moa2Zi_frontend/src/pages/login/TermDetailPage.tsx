import { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { getTermDetails } from "@/services/auth";
import { TermInfo } from "@/types/terms";
import ArrowLeftIcon from "@components/svgs/ArrowLeftIcon";

const TermDetailPage = () => {
  const { termId } = useParams<{ termId: string }>();
  const [termDetails, setTermDetails] = useState<TermInfo>();

  const fetchData = async () => {
    try {
      const response = await getTermDetails(termId);
      setTermDetails(response.data);
      // console.log(response.data);
    } catch (error) {
      // console.log(error);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleBack = () => {
    window.history.back();
  };

  return (
    <div className="flex flex-col w-full max-w-[600px] h-full min-h-screen mx-auto my-10 bg-white">
      <div className="fixed flex top-0 left-0 right-0 w-full max-w-[600px] h-16 mx-auto text-center items-center justify-center bg-neutral-100 border-x border-neutral-200">
        <div onClick={handleBack} className="flex items-center cursor-pointer">
          <ArrowLeftIcon className="absolute left-5 size-7" />
        </div>
        이용 약관
      </div>

      <div className="flex flex-col gap-3 pt-20 px-7 pc:px-10 pb-10">
        {termDetails ? (
          <>
            <div className="text-2xl font-bold">{termDetails.title}</div>
            <div className="text-md text-gray-500">{termDetails.subTitle}</div>

            {termDetails.termDetailList.map((detail) => (
              <div key={detail.termDetailId} className="pt-6">
                <div className="text-lg font-semibold">{detail.title}</div>
                <div className="pt-2 text-base leading-7 whitespace-pre-line">{detail.content}</div>
              </div>
            ))}
          </>
        ) : (
          <div className="text-center text-gray-500">약관 정보를 불러오는 중입니다...</div>
        )}
      </div>
    </div>
  );
};

export default TermDetailPage;
