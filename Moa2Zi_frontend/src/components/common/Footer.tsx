import { NavLink } from "react-router-dom";
import Tab from "@components/footer/Tab";
import ChallengeFillIcon from "@components/svgs/ChallengeFillIcon";
import ChallengeBlankIcon from "@components/svgs/ChallengeBlankIcon";
import LoungeFillIcon from "@components/svgs/LoungeFillIcon";
import LoungeBlankIcon from "@components/svgs/LoungeBlankIcon";
import AccountBookFillIcon from "@components/svgs/AccountBookFillIcon";
import AccountBookBlankIcon from "@components/svgs/AccountBookBlankIcon";
import ExpenseMapFillIcon from "@components/svgs/ExpenseMapFillIcon";
import ExpenseMapBlankIcon from "@components/svgs/ExpenseMapBlankIcon";
import ProfileFillIcon from "@components/svgs/ProfileFillIcon";
import ProfileBlankIcon from "@components/svgs/ProfileBlankIcon";

const Footer = () => {
  const tabs = [
    {
      to: "/challenge",
      label: "챌린쥐",
      fillIcon: ChallengeFillIcon,
      blankIcon: ChallengeBlankIcon,
    },
    {
      to: "/lounge",
      label: "라운쥐",
      fillIcon: LoungeFillIcon,
      blankIcon: LoungeBlankIcon,
    },
    {
      to: "/account-book",
      label: "가계부",
      fillIcon: AccountBookFillIcon,
      blankIcon: AccountBookBlankIcon,
    },
    {
      to: "/expense-map",
      label: "소비 발자국",
      fillIcon: ExpenseMapFillIcon,
      blankIcon: ExpenseMapBlankIcon,
    },
    {
      to: "/profile",
      label: "프로필",
      fillIcon: ProfileFillIcon,
      blankIcon: ProfileBlankIcon,
    },
  ];
  return (
    <footer className="fixed flex  justify-evenly bottom-0 left-0 right-0 w-full max-w-[600px] h-16 mx-auto bg-white pc:border-x border-t border-neutral-200 z-50">
      {tabs.map(({ to, label, fillIcon: FillIcon, blankIcon: BlankIcon }) => (
        <NavLink to={to} className="flex-1" key={to}>
          {({ isActive }) => (
            <Tab
              isActive={isActive}
              fillIconType={FillIcon}
              blankIconType={BlankIcon}
              label={label}
            />
          )}
        </NavLink>
      ))}
    </footer>
  );
};

export default Footer;
