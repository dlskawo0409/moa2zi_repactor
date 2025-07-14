import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import ExpenseCalendar from "@components/calendar/ExpenseCalendar";
import { DailySumWithDate } from "@/types/calendar";
import ExpenseInfo from "@components/calendar/ExpenseInfo";
import { getCalendarTransaction } from "@/services/calendar";

interface FriendCalenderProps {
  nickname: string;
}

const FriendCalender = ({ nickname }: FriendCalenderProps) => {
  const { memberId } = useParams();

  const [selectedDate, setSelectedDate] = useState<Date>(new Date());
  const [spendSum, setSpendSum] = useState<number>(0);
  const [amountDiffPrevMonth, setAmountDiffPrevMonth] = useState<number>(0);
  const [dailySumWithDate, setDailySumWithDate] = useState<DailySumWithDate[]>([]);

  const [formattedMonth, setFormattedMonth] = useState<string>(() => {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, "0");
    return `${year}${month}00`;
  });

  const selectedYear = selectedDate?.getFullYear() ?? new Date().getFullYear();
  const selectedMonth = (selectedDate?.getMonth() ?? new Date().getMonth()) + 1;

  const fetchData = async () => {
    try {
      const response = await getCalendarTransaction({
        memberId: memberId,
        transactionDate: formattedMonth,
      });
      // console.log(response.data);
      setSpendSum(response.data.spendSum);
      setAmountDiffPrevMonth(response.data.amountDiffPrevMonth);
      setDailySumWithDate(response.data.dailySumWithDate);
    } catch (error) {
      // console.log(error);
    }
  };

  useEffect(() => {
    fetchData();
  }, [formattedMonth]);

  return (
    <div>
      <div className="flex justify-center w-full gap-2 text-xl pc:text-2xl">
        <div className="text-primary-500 font-bold">{nickname}</div>
        <div className="font-semibold">님의 소비 달력</div>
      </div>
      <ExpenseCalendar
        setSelectedDate={setSelectedDate}
        dailySumWithDate={dailySumWithDate}
        setFormattedMonth={setFormattedMonth}
      />
      <ExpenseInfo
        selectedYear={selectedYear}
        selectedMonth={selectedMonth}
        spendSum={spendSum}
        amountDiffPrevMonth={amountDiffPrevMonth}
      />
    </div>
  );
};

export default FriendCalender;
