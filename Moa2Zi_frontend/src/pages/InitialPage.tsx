import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { motion, AnimatePresence } from "framer-motion";
import ExplosionParticles from "@components/onBoarding/ExplosionParticles";
import CommonButton from "@components/common/CommonButton";
import IntroMouseOnBoard from "@components/svgs/onBoarding/IntroMouseOnBoard";
import IconMouseOnBoard from "@components/svgs/onBoarding/IconMouseOnBoard";
import ExpenseMapOnBoard from "@components/svgs/onBoarding/ExpenseMapOnBoard";
import LoungeOnBoard from "@components/svgs/onBoarding/LoungeOnBoard";
import ChallengeOnBoard from "@components/svgs/onBoarding/ChallengeOnBoard";
import MyAccountOnBoard from "@components/svgs/onBoarding/MyAccountOnBoard";
import MyCardOnBoard from "@components/svgs/onBoarding/MyCardOnBoard";

const slides = [
  {
    title: "모앗쥐에 오신 걸 환영해요",
    description: "소비를 모으고 아낄 준비, 되셨나요?",
    ImageComponent: IntroMouseOnBoard,
  },
  {
    title: "카드·현금 통합 관리",
    description: "계좌, 카드 연동하여 관리해보세요!",
    ImageComponent: MyCardOnBoard,
  },
  {
    title: "예쁜 통계로 한눈에!",
    description: "이달의 지출, 예쁘고 알기 쉽게",
    ImageComponent: MyAccountOnBoard,
  },
  {
    title: "혼자서는 힘든 절약",
    description: "친구들과 함께 해보는건 어때요?",
    ImageComponent: LoungeOnBoard,
  },
  {
    title: "재미있게 절약할 수 없을까?",
    description: "무지출 챌린지부터 하루 만원 챌린지까지!",
    ImageComponent: ChallengeOnBoard,
  },
  {
    title: "어디에서 많이 썼지?",
    description: "소비 알림으로 과소비 방지!!",
    ImageComponent: ExpenseMapOnBoard,
  },
];

const finalSlide = {
  title: "모앗쥐 시작하기",
  ImageComponent: IconMouseOnBoard,
};

const variants = {
  initial: { opacity: 0, scale: 0.95, y: 30 },
  animate: { opacity: 1, scale: 1, y: 0, transition: { duration: 0.6 } },
  exit: { opacity: 0, scale: 1, y: -20, transition: { duration: 0.3 } },
};

const finalVariants = {
  initial: { y: 0 },
  animate: {
    y: [0, -20, 0, -10, 0],
    transition: {
      duration: 1.2,
      ease: "easeInOut",
      times: [0, 0.3, 0.6, 0.85, 1],
      repeat: Infinity,
      repeatDelay: 0.5,
    },
  },
  exit: { y: 0 },
};

export default function InitialPage() {
  const [page, setPage] = useState<number>(0);
  const isFirst = page === 0;
  const isFinal = page === slides.length;
  const slide = isFinal ? finalSlide : slides[page];
  const ImageComponent = slide.ImageComponent;
  const [hasInteracted, setHasInteracted] = useState<boolean>(false);

  const navigate = useNavigate();

  useEffect(() => {
    if (!hasInteracted || isFinal) return;

    const timer = setTimeout(() => {
      setPage((prev) => prev + 1);
    }, 1000);

    return () => clearTimeout(timer);
  }, [page, isFinal, hasInteracted]);

  return (
    <div className="flex items-center justify-center w-full h-screen bg-gradient-to-br from-orange-100 via-orange-200 to-orange-300 p-4">
      <AnimatePresence mode="wait">
        <motion.div
          key={page}
          variants={variants}
          initial="initial"
          animate="animate"
          exit="exit"
          onClick={() => {
            if (!hasInteracted) setHasInteracted(true);
          }}
          className="w-full max-w-md h-[85vh] rounded-2xl p-8 flex flex-col items-center text-center relative justify-center"
        >
          <motion.div
            variants={isFirst || isFinal ? finalVariants : {}}
            initial="initial"
            animate="animate"
            exit="exit"
          >
            <ImageComponent className="pc:w-auto w-full pc:max-h-[500px] h-full object-contain" />
          </motion.div>

          <h2 className="text-2xl font-bold text-gray-800 my-3">{slide.title}</h2>
          <p className="text-base text-gray-600">{slide.description}</p>

          {isFinal && (
            <>
              <ExplosionParticles />
              <div className="flex flex-col">
                <CommonButton
                  variant="primary"
                  onClick={() => navigate("/login", { viewTransition: true })}
                >
                  로그인하러 가기
                </CommonButton>
              </div>
            </>
          )}
        </motion.div>
      </AnimatePresence>
    </div>
  );
}
