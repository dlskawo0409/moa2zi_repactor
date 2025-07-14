// 20250403
export const formatDateToNumber = (date: Date) => {
  const year = date.getFullYear();
  const month = `0${date.getMonth() + 1}`.slice(-2);
  const day = `0${date.getDate()}`.slice(-2);
  return parseInt(`${year}${month}${day}`);
};

// 4월 3일 오후 5:23
export const formatKoreanDate = (dateString: string) => {
  const date = new Date(dateString);
  const month = date.getMonth() + 1;
  const day = date.getDate();
  let hour = date.getHours();
  const minute = String(date.getMinutes()).padStart(2, "0");

  const isPM = hour >= 12;
  const period = isPM ? "오후" : "오전";
  hour = hour % 12 || 12; // 0시는 12시로 표시

  return `${month}월 ${day}일 ${period} ${hour}:${minute}`;
};

// 오후 5:23
export const formatKoreanTime = (dateString: string) => {
  const date = new Date(dateString);
  let hour = date.getHours();
  const minute = String(date.getMinutes()).padStart(2, "0");

  const isPM = hour >= 12;
  const period = isPM ? "오후" : "오전";
  hour = hour % 12 || 12; // 0시는 12시로 표시

  return `${period} ${hour}:${minute}`;
};

// 2025-04-05 22:10
export const formatKebab = (dateString: string) => {
  const date = new Date(dateString);
  return `${date.getFullYear()}-${(date.getMonth() + 1)
    .toString()
    .padStart(2, "0")}-${date.getDate().toString().padStart(2, "0")} ${date
    .getHours()
    .toString()
    .padStart(2, "0")}:${date.getMinutes().toString().padStart(2, "0")}`;
};

// 20250404 => 2025.4.4
export const formatNumToDate = (dateStr: string | undefined) => {
  if (dateStr?.length !== 8) return "";

  const year = dateStr.slice(0, 4);
  const month = String(Number(dateStr.slice(4, 6))); // 앞에 0 제거
  const day = String(Number(dateStr.slice(6, 8)));

  return `${year}.${month}.${day}`;
};

// 20250404 => 2025.4.4
export const formatDateToKorean = (dateStr: string | undefined) => {
  if (dateStr?.length !== 8) return "";

  const year = dateStr.slice(0, 4);
  const month = String(Number(dateStr.slice(4, 6))); // 앞에 0 제거
  const day = String(Number(dateStr.slice(6, 8)));

  return `${year}년 ${month}월 ${day}일`;
};

// 130000 => 오후 1시 34분 2초
export const formatTimeToKorean = (timeStr: string) => {
  if (timeStr.length !== 6) return "";

  const hour = Number(timeStr.slice(0, 2));
  const minute = Number(timeStr.slice(2, 4));
  const second = Number(timeStr.slice(4, 6));

  const isPM = hour >= 12;
  const formattedHour = hour % 12 === 0 ? 12 : hour % 12;
  const period = isPM ? "오후" : "오전";

  let result = `${period} ${formattedHour}시`;

  if (minute !== 0) {
    result += ` ${minute}분`;
  }

  if (second !== 0) {
    result += ` ${second}초`;
  }

  return result;
};
