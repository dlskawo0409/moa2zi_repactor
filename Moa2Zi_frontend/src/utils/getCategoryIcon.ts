import { categoryImages } from "@/constants/categoryImages";

export const getCategoryIcon = (categoryName: string) => {
  const category = categoryImages.find((icon) => icon.name === categoryName);
  return category ? category.Icon : null;
};
