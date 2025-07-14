export interface SubCategory {
  subCategoryId: number;
  subCategoryName: string;
}

export interface TransactionItem {
  transactionId: number;
  transactionDate?: number;
  transactionTime?: string;
  categoryName?: string;
  subCategory?: SubCategory;
  emotion?: string;
  merchantName: string;
  transactionBalance: number;
  transactionType?: string;
  paymentMethod?: string;
  paymentType?: string;
}

export interface DayTransaction {
  dayId: number;
  dayOfWeek: string;
  transactionDate: number;
  transactionList: TransactionItem[];
}

export interface TransactionSummary {
  incomeSum: number;
  spendSum: number;
  totalSum: number;
  transactionWithDate: DayTransaction[];
}

export interface TransactionMethod {
  name: string;
  bankCode?: string;
  cardIssuerCode?: string;
}
