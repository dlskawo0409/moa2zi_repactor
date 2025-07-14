import { profileImages } from "@/constants/profileImages";

export const getProfileIcon = (iconName: string) => {
  const found = profileImages.find((icon) => icon.name === iconName);

  const IconComponent = found?.Component;
  return IconComponent;
};
