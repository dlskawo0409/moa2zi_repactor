import { useState, useEffect, Dispatch, SetStateAction, useRef, useCallback } from "react";
import LoungeMemberInputList from "@/components/lounge/LoungeMemberInputList";
import SelectedNicknameList from "@/components/lounge/SelectedNicknameList";
import { FriendList } from "@/types/lounge";
import { getFriendList } from "@/services/lounge";
import { useUserInfo } from "@/hooks/useUserInfo";

interface LoungeMemberInputProps {
  selectedMemberIds: number[];
  setSelectedMemberIds: Dispatch<SetStateAction<number[]>>;
}

const LoungeMemberInput = ({ selectedMemberIds, setSelectedMemberIds }: LoungeMemberInputProps) => {
  const { data } = useUserInfo();
  const observerRef = useRef<HTMLDivElement | null>(null);

  const [friendList, setFriendList] = useState<FriendList>({
    friendList: [],
    total: 0,
    size: 0,
    hasNext: false,
    next: null,
  });

  const [selectedNicknames, setSelectedNicknames] = useState<string[]>([]);
  const [loading, setLoading] = useState<boolean>(false);

  const fetchData = useCallback(
    async (isNew = false) => {
      if (loading || (!isNew && !friendList.hasNext)) return;

      setLoading(true);
      try {
        const response: { data: FriendList } = await getFriendList({
          requestId: data?.memberId,
          acceptId: data?.memberId,
          status: "ACCEPTED",
          size: 5,
          next: isNew ? null : friendList.next,
        });

        if (isNew) {
          setFriendList(response.data);
          // console.log(response.data);
        } else {
          setFriendList((prev) => ({
            ...response.data,
            friendList: [...prev.friendList, ...response.data.friendList],
          }));
        }
      } catch (error) {
        // console.log(error);
      } finally {
        setLoading(false);
      }
    },
    [data?.memberId, friendList.hasNext, friendList.next, loading],
  );

  useEffect(() => {
    if (data?.memberId) {
      fetchData(true);
    }
  }, [data?.memberId]);

  useEffect(() => {
    if (!observerRef.current || !friendList.hasNext) return;

    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting && !loading) {
          fetchData();
        }
      },
      { threshold: 1.0 },
    );

    observer.observe(observerRef.current);

    return () => {
      if (observerRef.current) observer.unobserve(observerRef.current);
    };
  }, [fetchData, friendList.hasNext, loading]);

  const members = friendList.friendList.map((friend) => {
    return friend.acceptMember.memberId === data?.memberId
      ? friend.requestMember
      : friend.acceptMember;
  });

  const handleCheckboxChange = (memberId: number, nickname: string) => {
    setSelectedMemberIds((prevSelected) => {
      const isSelected = prevSelected.includes(memberId);
      return isSelected
        ? prevSelected.filter((id) => id !== memberId)
        : prevSelected.length < 10
          ? [...prevSelected, memberId]
          : prevSelected;
    });

    setSelectedNicknames((prevNicknames) => {
      const isSelected = prevNicknames.includes(nickname);
      return isSelected
        ? prevNicknames.filter((nick) => nick !== nickname)
        : prevNicknames.length < 10
          ? [...prevNicknames, nickname]
          : prevNicknames;
    });
  };

  const handleRemoveNickname = (nicknameToRemove: string) => {
    setSelectedMemberIds((prevSelected) =>
      prevSelected.filter((id) => {
        const member = members.find((m) => m.nickname === nicknameToRemove);
        return member?.memberId !== id;
      }),
    );

    setSelectedNicknames((prevSelected) =>
      prevSelected.filter((nickname) => nickname !== nicknameToRemove),
    );
  };

  return (
    <div className="flex flex-col w-full px-5 gap-3">
      <div className="text-primary-500 font-semibold px-2">라운쥐 멤버</div>

      {selectedNicknames.length > 0 && (
        <SelectedNicknameList
          selectedNicknames={selectedNicknames}
          onRemove={handleRemoveNickname}
        />
      )}

      <div className="flex flex-col w-full gap-5 px-2 py-2 max-h-72 overflow-y-auto">
        {members.map((member) => (
          <LoungeMemberInputList
            key={member.memberId}
            nickname={member.nickname}
            memberId={member.memberId}
            profileImage={member.profileImage}
            checked={selectedMemberIds.includes(member.memberId)}
            onChange={() => handleCheckboxChange(member.memberId, member.nickname)}
          />
        ))}

        <div ref={observerRef} className="h-5" />
      </div>
    </div>
  );
};

export default LoungeMemberInput;
