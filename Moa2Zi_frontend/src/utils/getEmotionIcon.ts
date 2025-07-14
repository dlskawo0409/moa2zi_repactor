import { emotionIcons } from "@/constants/emotionIcons";

export const getEmotionIcon = (emotion: string) => {
  const found = emotionIcons.find((icon) => icon.name === emotion);

  const IconComponent = found?.Component;
  return IconComponent;
};
