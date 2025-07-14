import { useEffect, useRef } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { queryClient } from "@/lib/queryClient";

interface ExtendedOptions extends SockJS.Options {
  withCredentials: boolean;
}

interface SendChattingProps {
  loungeId?: string | number | undefined;
  onMessageReceived?: (message: any) => void;
}

export const useStompClient = ({ loungeId, onMessageReceived }: SendChattingProps) => {
  const wsUrl = "https://j12a403.p.ssafy.io/api/v1/ws-stomp";
  const stompClientRef = useRef<Client | null>(null);

  const initializeStompClient = () => {
    const accessToken = queryClient.getQueryData<string>(["accessToken"]);

    const stompClient = new Client({
      connectHeaders: {
        access: accessToken ?? "",
      },
      webSocketFactory: () =>
        new SockJS(wsUrl, null, {
          withCredentials: true,
        } as ExtendedOptions),
      onConnect: (frame) => {
        // console.log("Connected: " + frame);
        stompClient.subscribe(`/topic/chat/${loungeId}`, (message) => {
          const receivedMessage = JSON.parse(message.body);
          // console.log("📩 메시지 수신:", receivedMessage);
          if (onMessageReceived) {
            onMessageReceived(receivedMessage);
          }
        });
      },
      onStompError: (frame) => {
        // console.error("🚨 STOMP 에러:", frame.headers["message"], frame.body);
      },
      onWebSocketError: (event) => {
        // console.error("⚠️ WebSocket 에러:", event);
      },
    });

    stompClient.activate();
    stompClientRef.current = stompClient;
  };

  useEffect(() => {
    initializeStompClient();
    return () => {
      stompClientRef.current?.deactivate().then(() => console.log(""));
    };
  }, [loungeId]);

  const sendMessage = ({
    loungeId,
    memberId,
    messageType,
    content,
    localDateTime,
    nickname,
    profileImage,
  }: {
    loungeId: string | undefined;
    memberId: string | undefined;
    messageType: string;
    content?: string;
    localDateTime?: string;
    nickname?: string;
    profileImage?: string;
  }) => {
    if (!memberId) {
      console.warn("❌ userId 없음");
      return;
    }

    if (!stompClientRef.current || !stompClientRef.current.connected) {
      console.warn("🚨 STOMP 클라이언트가 연결되지 않음");
      return;
    }

    const accessToken = queryClient.getQueryData<string>(["accessToken"]);
    if (!accessToken) {
      console.warn("❌ accessToken 없음");
      return;
    }

    const message = {
      memberId,
      messageType,
      content,
      localDateTime,
      loungeId,
      nickname,
      profileImage,
    };

    // console.log(message);
    stompClientRef.current.publish({
      destination: "/app/chat/send",
      body: JSON.stringify(message),
      headers: {
        access: accessToken,
      },
    });
  };

  return { sendMessage };
};
