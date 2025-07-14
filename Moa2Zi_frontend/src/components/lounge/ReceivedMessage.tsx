import { useNavigate } from "react-router-dom";
import { formatKoreanTime } from "@/utils/formatDate";
import { getProfileIcon } from "@/utils/getProfileIcon";

interface ReceivedMessageProps {
  sender: string;
  message: string;
  profileImage: string;
  memberId: number;
  time: string;
}

const ReceivedMessage = ({
  sender,
  message,
  time,
  profileImage,
  memberId,
}: ReceivedMessageProps) => {
  const ParticipantIcon = getProfileIcon(profileImage);

  const navigate = useNavigate();

  return (
    <div className="flex px-5 py-1 gap-2">
      <div
        className="flex gap-3 self-start text-sm cursor-pointer"
        onClick={() => navigate(`/profile/friend/${memberId}`)}
      >
        {ParticipantIcon ? (
          <ParticipantIcon className="size-8 pc:size-10 border border-neutral-200 rounded-full" />
        ) : null}
      </div>
      <div className="flex flex-col mt-2 pc:pt-1 gap-1">
        <div className="text-xs pc:text-sm cursor-pointer" onClick={() => navigate(`/profile/friend/${memberId}`)}>
          {sender}
        </div>
        <div className="flex-1 bg-primary-300 max-w-52 pc:max-w-80 rounded-md py-2 px-4">
          {message}
        </div>
      </div>
      <div className="flex self-end text-xs text-neutral-600">{formatKoreanTime(time)}</div>
    </div>
  );
};

export default ReceivedMessage;
