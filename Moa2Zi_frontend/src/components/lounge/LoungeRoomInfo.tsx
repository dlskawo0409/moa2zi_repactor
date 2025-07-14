import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { ChevronDown, ChevronUp } from "lucide-react";
import { getProfileIcon } from "@/utils/getProfileIcon";
import { Participant } from "@/types/lounge";

interface LoungeRoomInfoProps {
  title: string;
  participantList: Participant[];
}
const LoungeRoomInfo = ({ title, participantList }: LoungeRoomInfoProps) => {
  const [isExpanded, setIsExpanded] = useState<boolean>(false);

  const navigate = useNavigate();

  const handleToggle = () => {
    setIsExpanded((prev) => !prev);
  };

  return (
    <div
      className={`flex fixed w-[94vw] pc:w-[575px] justify-between m-3 px-5 py-3 bg-white rounded-lg gap-2 border-2 ${isExpanded ? "border-primary-400" : "border-neutral-300"}`}
    >
      <div className="flex flex-col gap-2">
        <div className="flex gap-2 font-semibold">
          <div>{title}</div>
          <div>({participantList.length})</div>
        </div>
        {!isExpanded && (
          <div className="flex items-center">
            {participantList.slice(0, 4).map((p, i) => {
              const ParticipantIcon = getProfileIcon(p.profileImage);
              return (
                <div
                  key={p.memberId}
                  className={`w-6 h-6 pc:w-7 pc:h-7 rounded-full overflow-hidden bg-white border border-neutral-300 ${
                    i !== 0 ? "-ml-2" : ""
                  } flex items-center justify-center cursor-pointer`}
                  onClick={() => navigate(`/profile/friend/${p.memberId}`)}
                >
                  {ParticipantIcon ? <ParticipantIcon className="w-full h-full" /> : null}
                </div>
              );
            })}
            {participantList.length > 4 && (
              <div className="ml-2 text-xs pc:text-sm">+{participantList.length - 4}</div>
            )}
          </div>
        )}
        {isExpanded && (
          <div className="flex flex-col mt-2 gap-4">
            {participantList.map((p) => {
              const ParticipantIcon = getProfileIcon(p.profileImage);
              return (
                <div
                  key={p.memberId}
                  className="flex items-center gap-2 cursor-pointer"
                  onClick={() => navigate(`/profile/friend/${p.memberId}`)}
                >
                  <div className="w-6 h-6 pc:w-8 pc:h-8 rounded-full overflow-hidden bg-white border border-neutral-300">
                    {ParticipantIcon ? <ParticipantIcon className="w-full h-full" /> : null}
                  </div>
                  <div className="pc:text-sm">{p.nickname}</div>
                </div>
              );
            })}
          </div>
        )}
      </div>
      <div className="self-end">
        {isExpanded ? (
          <ChevronUp className="cursor-pointer" onClick={handleToggle} />
        ) : (
          <ChevronDown className="cursor-pointer" onClick={handleToggle} />
        )}
      </div>
    </div>
  );
};

export default LoungeRoomInfo;
