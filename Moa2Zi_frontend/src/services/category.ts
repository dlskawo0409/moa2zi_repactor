import apiClient from "@/services/http";
import { Category } from "@/types/category";

export const getCategories = async (
  categoryId: number | null,
  level: number,
  categoryType: string,
): Promise<Category[]> => {
  const response = await apiClient.get<Category[]>("/categories", {
    params: { categoryId, level, categoryType },
  });

  return response.data;
};

export const getSubCategories = async (
  categoryId: number | null,
  level: number,
  categoryType: string,
): Promise<Category[]> => {
  const response = await apiClient.get<Category[]>("/categories", {
    params: { categoryId, level, categoryType },
  });

  return response.data;
};
