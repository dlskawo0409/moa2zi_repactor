import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import LoungeTitleInput from "@components/lounge/LoungeTitleInput";
import LoungeMemberInput from "@components/lounge/LoungeMemberInput";
import LoungeEndDateInput from "@components/lounge/LoungeEndDateInput";
import LoungeGameDurationInput from "@components/lounge/LoungeGameDurationInput";
import CommonButton from "@components/common/CommonButton";
import { postLounge } from "@/services/lounge";

const LoungeStartPage = () => {
  const [title, setTitle] = useState<string>("");
  const [selectedMemberIds, setSelectedMemberIds] = useState<number[]>([]);
  const [date, setDate] = useState<string>("");
  const [duration, setDuration] = useState<string>("7");
  const [isFilled, setIsFilled] = useState<boolean>(false);

  const navigate = useNavigate();

  const handleCreateLounge = async () => {
    try {
      const response = await postLounge({
        title,
        participantList: selectedMemberIds,
        endTime: date,
        duration: duration,
      });

      // console.log(response.data);
      navigate("/lounge");
    } catch (error) {
      // console.log(error);
    }
  };

  const getMaxDuration = () => {
    if (!date) return 0;
    const today = new Date();
    const end = new Date(date);
    const diffTime = end.getTime() - today.getTime();
    return Math.floor(diffTime / (1000 * 60 * 60 * 24));
  };

  const maxDuration = getMaxDuration();

  useEffect(() => {
    const durationNum = Number(duration);

    const allFilled =
      title.trim().length > 0 &&
      selectedMemberIds.length > 0 &&
      date.trim().length > 0 &&
      !isNaN(durationNum) &&
      durationNum > 0 &&
      durationNum <= maxDuration;

    setIsFilled(allFilled);
  }, [title, selectedMemberIds, date, duration, maxDuration]);

  return (
    <div className="relative flex flex-col py-5 px-2 gap-7">
      <div className="flex justify-center">
        <div className="text-xl font-semibold">라운쥐 만들기</div>
      </div>
      <LoungeTitleInput title={title} setTitle={setTitle} />
      <LoungeMemberInput
        selectedMemberIds={selectedMemberIds}
        setSelectedMemberIds={setSelectedMemberIds}
      />
      <LoungeEndDateInput date={date} setDate={setDate} />
      <LoungeGameDurationInput duration={duration} setDuration={setDuration} endDate={date} />
      <CommonButton
        className="mx-5"
        variant="primary"
        disabled={!isFilled}
        onClick={() => handleCreateLounge()}
      >
        라운쥐 생성하기
      </CommonButton>
    </div>
  );
};

export default LoungeStartPage;
