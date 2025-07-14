interface SelectedNicknameListProps {
  selectedNicknames: string[];
  onRemove: (nickname: string) => void;
}

const SelectedNicknameList = ({ selectedNicknames, onRemove }: SelectedNicknameListProps) => {
  return (
    <div className="flex w-full">
      <div className="flex gap-2 w-full overflow-x-scroll scrollbar-hide">
        {selectedNicknames.map((nickname) => (
          <div
            key={nickname}
            className="flex text-sm px-2 py-1 rounded-full gap-3 bg-neutral-100 min-w-max"
          >
            <div className="flex">{nickname}</div>
            <div onClick={() => onRemove(nickname)} className="cursor-pointer">
              X
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default SelectedNicknameList;
