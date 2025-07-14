import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getTerms } from "@/services/auth";
import { Terms } from "@/types/terms";
import CheckIcon from "@components/svgs/CheckIcon";
import CommonButton from "@components/common/CommonButton";
import { useAgreement } from "@/hooks/useAgreement";

const TermPage = () => {
  const [terms, setTerms] = useState<Terms[]>([]);
  const { setAgreement } = useAgreement();

  const fetchData = async () => {
    try {
      const response = await getTerms();
      setTerms(response.data);
      // console.log(response);
    } catch (error) {
      // console.log(error);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const navigate = useNavigate();

  const [checkedTerms, setCheckedTerms] = useState<Record<number, boolean>>({});

  const allChecked = Object.values(checkedTerms).every(Boolean);
  const mandatoryChecked = terms
    .filter((term) => term.termType === "MANDATORY")
    .every((term) => checkedTerms[term.termId]);

  const toggleCheck = (termId: number) => {
    setCheckedTerms((prev) => ({ ...prev, [termId]: !prev[termId] }));
  };

  const toggleAllCheck = () => {
    const newValue = !allChecked;
    const updated = Object.fromEntries(terms.map((term) => [term.termId, newValue]));
    setCheckedTerms(updated);
  };

  useEffect(() => {
    const initialCheckedTerms = terms.reduce(
      (acc, term) => {
        acc[term.termId] = false;
        return acc;
      },
      {} as Record<number, boolean>,
    );
    setCheckedTerms(initialCheckedTerms);
  }, [terms]);

  return (
    <div className="flex flex-col h-screen w-full justify-center items-center gap-3 pc:gap-5">
      <h2 className="text-lg pc:text-xl font-bold">서비스 이용약관에 동의해주세요</h2>
      <div className="w-full text-sm pc:text-md">
        <div className="flex w-full items-center justify-between px-7 pc:px-10 border-b-[1px] border-neutral-200">
          <div className="flex gap-3 py-5 cursor-pointer" onClick={toggleAllCheck}>
            <CheckIcon
              className={allChecked ? "size-5 text-primary-500" : "size-5 text-neutral-300"}
            />
            <div className="flex">네, 모두 동의합니다.</div>
          </div>
        </div>
        {terms.map((term, index) => (
          <div
            key={term.termId}
            className={`flex w-full items-center justify-between px-7 pc:px-10 ${
              index === terms.length - 1 ? "border-b-[1px] border-neutral-200" : ""
            }`}
          >
            <div
              className="flex gap-3 py-4 cursor-pointer"
              onClick={() => toggleCheck(term.termId)}
            >
              <CheckIcon
                className={
                  checkedTerms[term.termId] ? "size-5 text-primary-500" : "size-5 text-neutral-300"
                }
              />
              <div className="flex gap-2">
                <span className="font-medium">{term.title}</span>
                {term.termType === "MANDATORY" ? (
                  <span className="text-negative-500">(필수)</span>
                ) : (
                  <span className="text-neutral-500">(선택)</span>
                )}
              </div>
            </div>
            <div
              className="underline text-neutral-400 cursor-pointer"
              onClick={() => navigate(`/terms/${term.termId}`)}
            >
              보기
            </div>
          </div>
        ))}
      </div>
      <div className="w-full px-5 pc:px-10 pt-10">
        <CommonButton
          variant="primary"
          className="w-full h-12"
          disabled={!mandatoryChecked}
          onClick={() => {
            setAgreement(true);
            navigate("/verification");
          }}
        >
          다음
        </CommonButton>
      </div>
      <div className="px-5 pc:px-10 text-sm text-neutral-400 text-center">
        "선택" 항목에 동의하지 않아도 서비스 이용이 가능합니다. 개인정보 수집 및 이용에 대한 동의를
        거부할 권리가 있으며 동의 거부 시 회원제 서비스 이용이 제한됩니다.
      </div>
    </div>
  );
};

export default TermPage;
