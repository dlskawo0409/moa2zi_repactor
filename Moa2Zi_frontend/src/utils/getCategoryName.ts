import { categoryNames } from "@/constants/categoryNames";

export const getCategoryName = (categoryId: string): string => {
  return categoryNames[categoryId] || categoryId;
};
