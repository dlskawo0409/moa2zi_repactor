export const formatTime = (time: { hour: string; minute: string; ampm: string }) => {
  let hour = parseInt(time.hour);
  if (time.ampm === "오후" && hour !== 12) hour += 12;
  if (time.ampm === "오전" && hour === 12) hour = 0;

  return `${String(hour).padStart(2, "0")}${time.minute}00`;
};
