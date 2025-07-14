import { useNavigate, useLocation } from "react-router-dom";
import { useState, useMemo } from "react";
import { motion } from "framer-motion";

type NavItem = {
  label: string;
  path: string;
};

const NavBar = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [activePath, setActivePath] = useState(location.pathname);

  const navItems: NavItem[] = useMemo(() => {
    if (location.pathname.startsWith("/challenge")) {
      return [
        { label: "추천 챌린지", path: "/challenge/recommend" },
        { label: "모든 챌린지", path: "/challenge" },
        { label: "나의 챌린지", path: "/challenge/my" },
      ];
    } else if (location.pathname.startsWith("/account-book")) {
      return [
        { label: "소비 달력", path: "/account-book/calendar" },
        { label: "나의 가계부", path: "/account-book" },
        { label: "셀프 용돈", path: "/account-book/pocket-money" },
        { label: "소비 보고서", path: "/account-book/statistics" },
      ];
    }
    return [];
  }, [location.pathname]);

  const activeIndex = useMemo(() => {
    // 예외 처리: /account-book/calendar로 시작하는 경우 "소비 달력" 인덱스
    if (location.pathname.startsWith("/account-book/calendar")) {
      return navItems.findIndex((item) => item.label === "소비 달력");
    }
    return navItems.findIndex((item) => item.path === activePath);
  }, [location.pathname, navItems, activePath]);

  return (
    <div className="h-10 pc:h-12 pc:my-5 my-3">
      <div className="relative flex justify-between items-center rounded-full h-full mx-3 p-1 shadow-md border border-primary-500 overflow-hidden">
        <motion.div
          layoutId="activeNavBg"
          className="absolute h-full bg-primary-500 rounded-full"
          initial={false}
          animate={{
            width: `${80 / navItems.length}%`,
            height: `60%`,
            left: `${(activeIndex / navItems.length) * 100 + 10 / navItems.length}%`,
          }}
          transition={{ type: "spring", stiffness: 500, damping: 50 }}
        />

        {navItems.map((item) => {
          const isActive =
            (location.pathname.startsWith("/account-book/calendar") &&
              item.label === "소비 달력") ||
            activePath === item.path;

          return (
            <button
              key={item.path}
              className={`relative text-xs pc:text-md flex-1 text-center px-2 py-1 rounded-full transition-all duration-500 cursor-pointer
                ${isActive ? "text-white font-extrabold" : "text-primary-500 font-extrabold"}`}
              onClick={() => {
                setActivePath(item.path);
                navigate(item.path);
              }}
            >
              {item.label}
            </button>
          );
        })}
      </div>
    </div>
  );
};

export default NavBar;
