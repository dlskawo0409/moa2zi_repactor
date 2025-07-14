import { useEffect, useState } from "react";
import { getDongList, getGugunList, getSidoList } from "@/services/map";
import {
  DropdownMenu,
  DropdownMenuTrigger,
  DropdownMenuContent,
  DropdownMenuItem,
} from "@/components/ui/dropdown-menu";

type Props = {
  onChange: (codes: {
    sidoCode: number | null;
    gugunCode: number | null;
    dongCode: number | null;
  }) => void;
};

const SiGuDongSelector = ({ onChange }: Props) => {
  const [sidoList, setSidoList] = useState<{ sidoCode: number; sidoName: string }[]>([]);
  const [gugunList, setGugunList] = useState<{ gugunCode: number; gugunName: string }[]>([]);
  const [dongList, setDongList] = useState<{ dongCode: number; dongName: string }[]>([]);

  const [selectedSido, setSelectedSido] = useState<number | null>(null);
  const [selectedGugun, setSelectedGugun] = useState<number | null>(null);
  const [selectedDong, setSelectedDong] = useState<number | null>(null);

  useEffect(() => {
    getSidoList().then(setSidoList);
  }, []);

  useEffect(() => {
    if (selectedSido !== null) {
      getGugunList(selectedSido).then((res) => {
        setGugunList(res);
        setSelectedGugun(null);
        setDongList([]);
        setSelectedDong(null);
      });
    } else {
      setGugunList([]);
      setSelectedGugun(null);
      setDongList([]);
      setSelectedDong(null);
    }
  }, [selectedSido]);

  useEffect(() => {
    if (selectedGugun !== null) {
      getDongList(selectedGugun).then((res) => {
        setDongList(res);
        setSelectedDong(null);
      });
    } else {
      setDongList([]);
      setSelectedDong(null);
    }
  }, [selectedGugun]);

  useEffect(() => {
    onChange({
      sidoCode: selectedSido,
      gugunCode: selectedGugun,
      dongCode: selectedDong,
    });
  }, [selectedSido, selectedGugun, selectedDong]);

  const renderMenu = (
    label: string,
    selected: string | null,
    list: { code: number; name: string }[],
    onSelect: (code: number | null) => void,
    disabled?: boolean,
  ) => (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <button
          disabled={disabled}
          className={`flex justify-center rounded px-2 py-1 text-sm bg-white min-w-[120px] text-left ${
            disabled ? "opacity-50 cursor-not-allowed" : ""
          }`}
        >
          {selected ?? label}
        </button>
      </DropdownMenuTrigger>
      <DropdownMenuContent side="top" className="max-h-[200px] overflow-y-auto">
        {/* 기본 선택 가능 항목 */}
        <DropdownMenuItem onSelect={() => onSelect(null)} className="text-gray-400 cursor-pointer">
          {label}
        </DropdownMenuItem>

        {list.map((item) => (
          <DropdownMenuItem
            key={item.code}
            onSelect={() => onSelect(item.code)}
            className="cursor-pointer"
          >
            {item.name}
          </DropdownMenuItem>
        ))}
      </DropdownMenuContent>
    </DropdownMenu>
  );

  return (
    <div className="flex items-center fixed h-10 bottom-20 left-1/2 -translate-x-1/2 z-[50] bg-white border border-primary-100 rounded-full shadow-md px-4 py-2 ">
      {renderMenu(
        "시/도 선택",
        sidoList.find((s) => s.sidoCode === selectedSido)?.sidoName ?? null,
        sidoList.map((s) => ({ code: s.sidoCode, name: s.sidoName })),
        setSelectedSido,
      )}
      {renderMenu(
        "군/구 선택",
        gugunList.find((g) => g.gugunCode === selectedGugun)?.gugunName ?? null,
        gugunList.map((g) => ({ code: g.gugunCode, name: g.gugunName })),
        setSelectedGugun,
        selectedSido === null,
      )}
      {renderMenu(
        "동 선택",
        dongList.find((d) => d.dongCode === selectedDong)?.dongName ?? null,
        dongList.map((d) => ({ code: d.dongCode, name: d.dongName })),
        setSelectedDong,
        selectedGugun === null,
      )}
    </div>
  );
};

export default SiGuDongSelector;
