import AwesomeMouseIcon from "@components/svgs/calendar/AwesomeMouseIcon";
import HappyMouseIcon from "@components/svgs/calendar/HappyMouseIcon";
import SurprisedMouseIcon from "@components/svgs/calendar/SurprisedMouseIcon";
import AngryMouseIcon from "@components/svgs/calendar/AngryMouseIcon";
import SadMouseIcon from "@components/svgs/calendar/SadMouseIcon";
import { EMOTION } from "@/types/calendar";
import { SVGProps } from "@/types/svg";

interface EmotionIconBubbleProps {
  emotion: EMOTION;
  onSelect?: (emotion: EMOTION) => void;
}

const emotionMap: {
  [key in Exclude<EMOTION, null>]: {
    icon: (props: SVGProps) => JSX.Element;
    bgColor: string;
  };
} = {
  AWESOME: {
    icon: AwesomeMouseIcon,
    bgColor: "#F8B6B6",
  },
  HAPPY: {
    icon: HappyMouseIcon,
    bgColor: "#F8D2B6",
  },
  SURPRISE: {
    icon: SurprisedMouseIcon,
    bgColor: "#FFF599",
  },
  ANGRY: {
    icon: AngryMouseIcon,
    bgColor: "#CEEBCA",
  },
  SAD: {
    icon: SadMouseIcon,
    bgColor: "#C3CCF8",
  },
};

const EmotionIconBubble = ({ emotion, onSelect }: EmotionIconBubbleProps) => {
  if (!emotion) return null;

  const { icon: Icon, bgColor } = emotionMap[emotion as Exclude<EMOTION, null>];

  return (
    <div
      className="flex justify-center items-center rounded-full w-12 h-12"
      style={{ backgroundColor: bgColor }}
      onClick={() => onSelect?.(emotion)}
    >
      <Icon className="size-full" />
    </div>
  );
};
export default EmotionIconBubble;
