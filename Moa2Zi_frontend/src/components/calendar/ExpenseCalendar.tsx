import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";
import { formatDateToNumber } from "@/utils/formatDate";
import { useUserInfo } from "@/hooks/useUserInfo";
import { DailySumWithDate } from "@/types/calendar";
import "@/styles/CustomCalendar.css";

// react-calendar 타입 정의에서 가져온 타입
type ValuePiece = Date | null;
type Value = ValuePiece | [ValuePiece, ValuePiece];

interface ExpenseCalendarProps {
  setSelectedDate: (date: Date) => void;
  dailySumWithDate: DailySumWithDate[];
  setFormattedMonth: (date: string) => void;
}

const ExpenseCalendar = ({
  setSelectedDate,
  dailySumWithDate,
  setFormattedMonth,
}: ExpenseCalendarProps) => {
  const { data } = useUserInfo();
  const { memberId } = useParams();
  const [value, setValue] = useState<Value>(new Date());

  const navigate = useNavigate();

  const handleDateChange = (value: Value) => {
    if (Array.isArray(value)) return;

    if (value) {
      setValue(value);

      const dateNumber = formatDateToNumber(value);
      const matched = dailySumWithDate.find((item) => item.date === dateNumber);

      if (matched && matched.dayId !== undefined) {
        // console.log("선택된 날짜:", dateNumber);
        // console.log(`${data?.memberId}/${dateNumber}`);
        if (memberId) {
          navigate(`/account-book/calendar/day/${matched.dayId}/${memberId}/${dateNumber}`);
        } else {
          navigate(`/account-book/calendar/day/${matched.dayId}/${data?.memberId}/${dateNumber}`);
        }
      } else {
        // console.log("선택한 날짜에 해당하는 dayId가 없습니다.");
      }
    }
  };

  const handleMonthChange = ({ activeStartDate }: { activeStartDate: Date | null }) => {
    if (activeStartDate) {
      setSelectedDate(activeStartDate);

      const year = activeStartDate.getFullYear();
      const month = String(activeStartDate.getMonth() + 1).padStart(2, "0");
      const formatted = `${year}${month}00`;
      setFormattedMonth(formatted);

      // console.log("현재 보고 있는 달:", formatted);
    }
  };

  return (
    <div className="relative px-5 pc:px-10">
      <Calendar
        value={value}
        onChange={handleDateChange}
        onActiveStartDateChange={handleMonthChange}
        locale="ko-KR"
        calendarType="gregory"
        formatDay={(locale, date) => String(date.getDate())}
        prev2Label={null}
        next2Label={null}
        tileContent={({ date, view }) => {
          if (view !== "month") return null;

          const dateNum = formatDateToNumber(date);
          const matched = dailySumWithDate.find((item) => item.date === dateNum);

          return matched ? (
            <div className="calendar-content text-xxxs pc:text-xxs text-neutral-900">
              {matched.sum.toLocaleString()}
            </div>
          ) : null;
        }}
        tileClassName={({ date, view }) => {
          if (view === "month") {
            const day = date.getDay();
            if (day === 0) {
              return "sunday";
            }
            if (day === 6) {
              return "saturday";
            }
          }
          return null;
        }}
      />
    </div>
  );
};

export default ExpenseCalendar;
