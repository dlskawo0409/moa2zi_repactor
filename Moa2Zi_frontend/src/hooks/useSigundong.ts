import { useCallback } from "react";
import { getDongList, getGugunList, getLocationByCodes, getSidoList } from "@/services/map";

export const useMoveMapByLocation = () => {
  const getSidoName = async (sidoCode: number) => {
    const sidoList = await getSidoList();
    return sidoList.find((s) => s.sidoCode === sidoCode)?.sidoName || "";
  };

  const getGugunName = async (sidoCode: number, gugunCode: number) => {
    const gugunList = await getGugunList(sidoCode);
    return gugunList.find((g) => g.gugunCode === gugunCode)?.gugunName || "";
  };

  const getDongName = async (gugunCode: number, dongCode: number) => {
    const dongList = await getDongList(gugunCode);
    return dongList.find((d) => d.dongCode === dongCode)?.dongName || "";
  };

  const moveMapByAddress = useCallback(
    async (
      sidoCode: number | null,
      gugunCode: number | null,
      dongCode: number | null,
      map: naver.maps.Map | null,
      setLatLng: (lat: number, lng: number) => void,
    ) => {
      if (!sidoCode) return;

      const sidoName = await getSidoName(sidoCode);
      const gugunName = gugunCode ? await getGugunName(sidoCode, gugunCode) : "";
      const dongName = dongCode ? await getDongName(gugunCode!, dongCode) : "";

      const address = [sidoName, gugunName, dongName].filter(Boolean).join(" ");
      const location = await getLocationByCodes(address);

      if (location && map) {
        const latlng = new naver.maps.LatLng(location.latitude, location.longitude);
        map.setCenter(latlng);

        // 줌 설정
        const zoom = dongName ? 15 : gugunName ? 13 : 11;
        map.setZoom(zoom);

        setLatLng(location.latitude, location.longitude);
      }
    },
    [],
  );

  return { moveMapByAddress };
};
