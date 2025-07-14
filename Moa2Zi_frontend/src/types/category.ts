export interface SubCategory {
  subCategoryId: number | null;
  subCategoryName: string | null;
}

export interface Category {
  categoryId: number;
  categoryName: string;
  categoryList?: SubCategory[];
}
