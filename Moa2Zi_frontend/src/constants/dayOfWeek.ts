export enum DayOfWeek {
  TODAY = "오늘",
  SUNDAY = "일요일",
  MONDAY = "월요일",
  TUESDAY = "화요일",
  WEDNESDAY = "수요일",
  THURSDAY = "목요일",
  FRIDAY = "금요일",
  SATURDAY = "토요일",
}

export const getKoreanDayOfWeek = (day: string): string => {
  return DayOfWeek[day as keyof typeof DayOfWeek] || day;
};
