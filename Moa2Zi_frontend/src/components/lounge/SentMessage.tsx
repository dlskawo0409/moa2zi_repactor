import { formatKoreanTime } from "@/utils/formatDate";

interface SentMessageProps {
  message: string;
  time: string;
}

const SentMessage = ({ message, time }: SentMessageProps) => {
  return (
    <div className="flex self-end px-5 pt-4 gap-2">
      <div className="flex self-end text-xs text-neutral-600">{formatKoreanTime(time)}</div>
      <div className="flex-1 bg-neutral-100 max-w-64 pc:max-w-80 rounded-md py-2 px-4 whitespace-pre-wrap break-words break-all">
        {message}
      </div>
    </div>
  );
};

export default SentMessage;
