import { useEffect, useRef, useState, useCallback } from "react";
import { useParams } from "react-router-dom";
import LoungeRoomInfo from "@components/lounge/LoungeRoomInfo";
import LoungeChatInputArea from "@components/lounge/LoungeChatInputArea";
import SentMessage from "@components/lounge/SentMessage";
import ReceivedMessage from "@components/lounge/ReceivedMessage";
import { LoungeInfo, ChatData, ChatMessage, ChatItem } from "@/types/lounge";
import { getLoungeMembers, getChatList, putChatRead } from "@/services/lounge";
import { useUserInfo } from "@/hooks/useUserInfo";
import { useStompClient } from "@/hooks/useStompClient";

const LoungeRoomPage = () => {
  const { loungeId } = useParams<{ loungeId: string }>();
  const { data } = useUserInfo();

  const [loungeInfo, setLoungeInfo] = useState<LoungeInfo>({
    loungeId: 0,
    title: "",
    loungeStatus: "COMPLETED",
    participantList: [],
  });

  const [chatData, setChatData] = useState<ChatData>({
    chatList: [],
    total: 0,
    size: 10,
    hasNext: true,
    next: "",
  });

  const scrollContainerRef = useRef<HTMLDivElement>(null);
  const isFetchingRef = useRef(false);
  const isInitialLoad = useRef(true);

  // 라운지 정보 가져오기
  const fetchData = async () => {
    try {
      const response = await getLoungeMembers(loungeId);
      setLoungeInfo(response.data);
    } catch (error) {
      // console.error(error);
    }
  };

  // 채팅 목록 가져오기
  const fetchChatData = async () => {
    if (isFetchingRef.current || (!chatData.hasNext && !isInitialLoad.current)) return;

    isFetchingRef.current = true;
    const prevScrollHeight = scrollContainerRef.current?.scrollHeight || 0;

    try {
      const response = await getChatList({
        loungeId,
        size: 20,
        next: isInitialLoad.current ? "" : chatData.next,
      });
      // console.log(response.data);
      if (response.data) {
        setChatData((prev) => ({
          ...prev,
          chatList: [...prev.chatList, ...response.data.chatList],
          total: response.data.total,
          size: response.data.size,
          hasNext: response.data.hasNext,
          next: response.data.next,
        }));

        handleChatRead(response.data.chatList[0].timeStamp);

        setTimeout(() => {
          const container = scrollContainerRef.current;
          if (!container) return;

          if (isInitialLoad.current) {
            container.scrollTop = container.scrollHeight;
            isInitialLoad.current = false;
          } else {
            const scrollHeightDiff = container.scrollHeight - prevScrollHeight;
            container.scrollTop += scrollHeightDiff;
          }
        }, 0);
      }
    } catch (error) {
      // console.error(error);
    } finally {
      isFetchingRef.current = false;
    }
  };

  useEffect(() => {
    fetchData();
    fetchChatData();
  }, []);

  // 하단으로 자동 스크롤
  const handleScroll = () => {
    const container = scrollContainerRef.current;
    if (container && container.scrollTop === 0 && chatData.hasNext) {
      fetchChatData();
    }
  };

  // 메세지 수신 처리
  const handleMessageReceived = useCallback((newMessage: ChatMessage) => {
    const now = new Date();
    const offset = now.getTimezoneOffset() * 60000;
    const localISOTime = new Date(now.getTime() - offset).toISOString().slice(0, 19);

    const chatItem: ChatItem = {
      chatId: newMessage.timeStamp,
      content: newMessage.content,
      nickname: newMessage.nickname,
      profileImage: newMessage.profileImage,
      timeStamp: localISOTime,
      memberId: newMessage.memberId,
      loungeId: loungeId,
      messageType: "CHAT",
    };

    handleChatRead(newMessage.timeStamp);

    setChatData((prev) => {
      const updated = {
        ...prev,
        chatList: [chatItem, ...prev.chatList],
      };

      // 다음 렌더링 이후 자동 스크롤
      setTimeout(() => {
        const container = scrollContainerRef.current;
        if (container) {
          container.scrollTop = container.scrollHeight;
        }
      }, 20);

      return updated;
    });
  }, []);

  const { sendMessage } = useStompClient({
    loungeId,
    onMessageReceived: handleMessageReceived,
  });

  // 메세지 읽음 처리
  const handleChatRead = async (lastReadTime: string) => {
    try {
      const response = await putChatRead({ loungeId, lastReadTime });
      // console.log(response.data);
    } catch (error) {
      // console.log(error);
    }
  };

  return (
    <div className="flex flex-col text-sm h-screen">
      <LoungeRoomInfo title={loungeInfo.title} participantList={loungeInfo.participantList} />

      <div
        ref={scrollContainerRef}
        onScroll={handleScroll}
        className="flex flex-col overflow-y-scroll mt-28 pb-20 h-full px-0 scrollbar-hide"
      >
        {[...chatData.chatList].reverse().map((chat, index) => {
          const isSentByCurrentUser = chat.memberId === data?.memberId;

          return isSentByCurrentUser ? (
            <SentMessage key={index} message={chat.content} time={chat.timeStamp.toString()} />
          ) : (
            <ReceivedMessage
              key={index}
              sender={chat.nickname}
              message={chat.content}
              time={chat.timeStamp.toString()}
              memberId={chat.memberId}
              profileImage={chat.profileImage}
            />
          );
        })}
      </div>

      <LoungeChatInputArea loungeStatus={loungeInfo.loungeStatus} sendMessage={sendMessage} />
    </div>
  );
};

export default LoungeRoomPage;
