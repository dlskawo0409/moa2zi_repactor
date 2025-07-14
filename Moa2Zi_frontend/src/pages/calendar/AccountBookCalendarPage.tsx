import { useEffect, useState } from "react";
import ExpenseCalendar from "@components/calendar/ExpenseCalendar";
import ExpenseInfo from "@components/calendar/ExpenseInfo";
import NavBar from "@components/common/NavBar";
import { useUserInfo } from "@/hooks/useUserInfo";
import { getCalendarTransaction } from "@/services/calendar";
import { DailySumWithDate } from "@/types/calendar";

const AccountBookCalendarPage = () => {
  const { data, refetch } = useUserInfo();
  const [selectedDate, setSelectedDate] = useState<Date>(new Date());

  const [formattedMonth, setFormattedMonth] = useState<string>(() => {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, "0");
    return `${year}${month}00`;
  });

  const [spendSum, setSpendSum] = useState<number>(0);
  const [amountDiffPrevMonth, setAmountDiffPrevMonth] = useState<number>(0);
  const [dailySumWithDate, setDailySumWithDate] = useState<DailySumWithDate[]>([]);

  const selectedYear = selectedDate?.getFullYear() ?? new Date().getFullYear();
  const selectedMonth = (selectedDate?.getMonth() ?? new Date().getMonth()) + 1;

  const fetchData = async () => {
    try {
      if (data) {
        const response = await getCalendarTransaction({
          memberId: data?.memberId,
          transactionDate: formattedMonth,
        });
        // console.log(response.data);
        setSpendSum(response.data.spendSum);
        setAmountDiffPrevMonth(response.data.amountDiffPrevMonth);
        setDailySumWithDate(response.data.dailySumWithDate);
      }
    } catch (error) {
      // console.log(error);
    }
  };

  useEffect(() => {
    if (data) {
      fetchData();
    }
  }, [formattedMonth, data]);

  useEffect(() => {
    if (data) {
      refetch();
    }
  }, []);

  return (
    <div>
      <NavBar />
      <div className="flex justify-center top-16 left-12 w-full gap-2 pt-3 pc:mt-3 text-lg pc:text-2xl">
        <div className="text-primary-500 font-bold">{data?.nickname}</div>
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

export default AccountBookCalendarPage;
