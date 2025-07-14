import { getProfileIcon } from "@/utils/getProfileIcon";

interface LoungeMemberInputListProps {
  nickname: string;
  memberId: number;
  profileImage: string;
  checked: boolean;
  onChange: () => void;
}

const LoungeMemberInputList = ({
  nickname,
  memberId,
  profileImage,
  checked,
  onChange,
}: LoungeMemberInputListProps) => {
  const Icon = getProfileIcon(profileImage);

  return (
    <label
      htmlFor={`member-${memberId}`}
      className="flex items-center justify-between w-full gap-2 cursor-pointer"
    >
      <div className="flex gap-3 items-center">
        {Icon && <Icon className="size-8 rounded-full border border-neutral-200" />}
        <div className="text-sm">{nickname}</div>
      </div>
      <input
        type="checkbox"
        id={`member-${memberId}`}
        name="selectedMembers"
        value={memberId}
        checked={checked}
        onChange={onChange}
        className="cursor-pointer"
      />
    </label>
  );
};

export default LoungeMemberInputList;
