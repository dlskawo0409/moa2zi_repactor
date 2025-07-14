import { useRef, useEffect, RefObject } from "react";
import { AMPM, HOURS, MINUTES } from "@/constants/time";

interface TimePickerProps {
  ampm: string;
  hour: string;
  minute: string;
  onChange: (ampm: string, hour: string, minute: string) => void;
}

const TimePicker = ({ ampm, hour, minute, onChange }: TimePickerProps) => {
  const ampmRef = useRef<HTMLDivElement>(null);
  const hourRef = useRef<HTMLDivElement>(null);
  const minuteRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    // 스크롤이 감지되었을 때 가장 중앙에 있는 요소를 찾고 선택된 값으로 업데이트
    const handleScroll = (
      ref: RefObject<HTMLDivElement>, // 스크롤이 감지될 요소의 Ref
      currentValue: string, // 현재 선택된 값 (ampm, hour, minute)
      type: "ampm" | "hour" | "minute",
    ) => {
      if (!ref.current) return;
      const container = ref.current;
      const items = Array.from(container.children) as HTMLDivElement[];

      // 컨테이너 중앙의 y 좌표
      const containerCenter = container.getBoundingClientRect().top + container.clientHeight / 2;

      // 컨테이너 중앙과 각 요소의 중앙 간의 최소 거리
      let minDiff = Infinity;
      let selectedValue = currentValue;

      items.forEach((item) => {
        const rect = item.getBoundingClientRect();
        // 각 요소의 중심과 컨테이너 중심의 거리 계산
        const centerDiff = Math.abs(rect.top + rect.height / 2 - containerCenter);

        // 최소 거리 최신화
        if (centerDiff < minDiff && item.innerText.trim()) {
          minDiff = centerDiff;
          selectedValue = item.innerText;
        }
      });

      // 변경값 적용
      if (selectedValue !== currentValue) {
        if (type === "ampm") onChange(selectedValue, hour, minute);
        if (type === "hour") onChange(ampm, selectedValue, minute);
        if (type === "minute") onChange(ampm, hour, selectedValue);
      }
    };

    const ampmScroll = () => handleScroll(ampmRef, ampm, "ampm");
    const hourScroll = () => handleScroll(hourRef, hour, "hour");
    const minuteScroll = () => handleScroll(minuteRef, minute, "minute");

    ampmRef.current?.addEventListener("scroll", ampmScroll);
    hourRef.current?.addEventListener("scroll", hourScroll);
    minuteRef.current?.addEventListener("scroll", minuteScroll);

    return () => {
      ampmRef.current?.removeEventListener("scroll", ampmScroll);
      hourRef.current?.removeEventListener("scroll", hourScroll);
      minuteRef.current?.removeEventListener("scroll", minuteScroll);
    };
  }, [ampm, hour, minute, onChange]);

  return (
    <div className="w-full">
      <div className="w-full flex gap-4 justify-between">
        {/* AM/PM 선택 */}
        <div
          ref={ampmRef}
          className="flex flex-col w-full overflow-y-auto max-h-44 snap-y snap-mandatory scrollbar-hide"
        >
          <div className="min-h-11 mt-10"></div>
          <div className="min-h-11"></div>
          {AMPM.map((item, idx) => (
            <div
              key={idx}
              className={`p-1 text-center snap-center transition ${item === ampm ? "text-xl font-bold text-primary-500" : "text-gray-500"}`}
            >
              {item}
            </div>
          ))}
          <div className="min-h-11"></div>
          <div className="min-h-11 mb-10"></div>
        </div>

        {/* 시간 선택 */}
        <div
          ref={hourRef}
          className="flex flex-col w-full overflow-y-auto max-h-44 snap-y snap-mandatory scrollbar-hide"
        >
          <div className="min-h-11 mt-10"></div>
          <div className="min-h-11"></div>
          {HOURS.map((h, idx) => (
            <div
              key={idx}
              className={`p-1 text-center snap-center transition ${h === hour ? "text-xl font-bold text-primary-500" : "text-gray-500"}`}
            >
              {h}
            </div>
          ))}
          <div className="min-h-11"></div>
          <div className="min-h-11 mb-10"></div>
        </div>

        {/* 분 선택 */}
        <div
          ref={minuteRef}
          className="flex flex-col w-full overflow-y-auto max-h-44 snap-y snap-mandatory scrollbar-hide"
        >
          <div className="min-h-11 mt-10"></div>
          <div className="min-h-11"></div>
          {MINUTES.map((m, idx) => (
            <div
              key={idx}
              className={`p-1 text-center snap-center transition ${m === minute ? "text-xl font-bold text-primary-500" : "text-gray-500"}`}
            >
              {m}
            </div>
          ))}
          <div className="min-h-11"></div>
          <div className="min-h-11 mb-10"></div>
        </div>
      </div>
    </div>
  );
};

export default TimePicker;
