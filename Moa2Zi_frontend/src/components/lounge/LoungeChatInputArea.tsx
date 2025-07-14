import { useState, useRef, ChangeEvent, KeyboardEvent } from "react";
import { useParams } from "react-router-dom";
import LoungeChatGameList from "@/components/lounge/LoungeChatGameList";
import SendIcon from "@components/svgs/SendIcon";
import GameIcon from "@components/svgs/GameIcon";
import { LoungeStatus } from "@/types/lounge";
import { useUserInfo } from "@/hooks/useUserInfo";

interface LoungeChatInputAreaProps {
  loungeStatus: LoungeStatus;
  sendMessage: ({
    loungeId,
    memberId,
    messageType,
    content,
    localDateTime,
    nickname,
  }: {
    loungeId: string;
    memberId: string | undefined;
    messageType: string;
    content?: string;
    localDateTime?: string;
    nickname?: string;
    profileImage?: string;
  }) => void;
}

const LoungeChatInputArea = ({ loungeStatus, sendMessage }: LoungeChatInputAreaProps) => {
  const { loungeId } = useParams<{ loungeId: string }>();
  const { data } = useUserInfo();
  const messageInputRef = useRef<HTMLTextAreaElement | null>(null);
  const [message, setMessage] = useState<string>("");
  const [expanded, setExpanded] = useState<boolean>(false);

  const handleInputChange = (e: ChangeEvent<HTMLTextAreaElement>): void => {
    setMessage(e.target.value);
    const textarea = e.target;
    textarea.style.height = "auto";
    textarea.style.height = `${textarea.scrollHeight}px`;
  };

  const handleSendMessage = () => {
    const trimmed = message.trim();
    if (!trimmed) return;

    // 메시지 전송
    const now = new Date();
    const offset = now.getTimezoneOffset() * 60000;
    const localISOTime = new Date(now.getTime() - offset).toISOString().slice(0, 19);

    sendMessage({
      loungeId: loungeId!,
      memberId: data?.memberId?.toString(),
      messageType: "CHAT",
      content: trimmed,
      localDateTime: localISOTime,
      nickname: data?.nickname,
      profileImage: data?.profileImage,
    });

    setMessage("");
    if (messageInputRef.current) {
      messageInputRef.current.style.height = "auto";
    }
  };

  const handleKeyDown = (e: KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  const toggleExpand = () => {
    setExpanded((prev) => !prev);
  };

  return (
    <div className="fixed w-full pc:w-[598px] bottom-16 flex flex-col items-center bg-white border-t border-neutral-200 gap-2 transition-all duration-300 ease-in-out">
      <div className="flex flex-col items-center w-full">
        <div className="flex items-center w-full gap-2 p-3">
          <div
            className="flex items-center justify-center border shadow-lg size-8 rounded-full cursor-pointer"
            onClick={toggleExpand}
          >
            <GameIcon className="size-4" />
          </div>
          <div className="flex items-center justify-center bg-neutral-100 size-8 rounded-full cursor-pointer">
            +
          </div>
          <textarea
            ref={messageInputRef}
            value={message}
            onChange={handleInputChange}
            onKeyDown={handleKeyDown}
            className="flex-1 border border-neutral-300 rounded-md px-3 pr-12 scrollbar-hide py-2 focus:outline-none text-sm resize-none min-h-[2.5rem] max-h-[6rem]"
            placeholder={loungeStatus === "TERMINATED" ? "라운쥐 기간이 끝났어요" : "메시지 입력"}
            rows={1}
            disabled={loungeStatus === "TERMINATED"}
          />
          <div
            className={`absolute right-6 size-6 ${
              loungeStatus === "TERMINATED" ? "cursor-not-allowed opacity-50" : "cursor-pointer"
            }`}
            onClick={handleSendMessage}
          >
            <SendIcon className="stroke-neutral-500 fill-primary-500" />
          </div>
        </div>
        <div className="w-full h-full">{expanded && <LoungeChatGameList />}</div>
      </div>
    </div>
  );
};

export default LoungeChatInputArea;
