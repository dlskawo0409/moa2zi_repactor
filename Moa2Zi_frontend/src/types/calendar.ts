export interface DailySumWithDate {
  date: number;
  dayId: number;
  sum: number;
}

export type EMOTION = "AWESOME" | "HAPPY" | "SAD" | "ANGRY" | "SURPRISE" | null;

export interface SubCategory {
  subCategoryId: number;
  subCategoryName: string;
}

export interface TransactionList {
  categoryName: string;
  subCategory: SubCategory;
  emotion: EMOTION;
  merchantName: string;
  paymentType: string;
  transactionBalance: number;
  transactionId: number;
  transactionType: string;
  memo: string;
  transactionTime: string;
}
