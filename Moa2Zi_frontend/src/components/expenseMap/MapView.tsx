import { useEffect, useRef, useState } from "react";
import ReactDOMServer from "react-dom/server";
import { useMoveMapByLocation } from "@/hooks/useSigundong";
import { getClusterMarkers } from "@/services/map";
import FootPrintIcon from "@components/svgs/FootPrintIcon";
import CurrentLocation from "@/components/expenseMap/CurrentLocation";
import TransactionDetailDrawer from "@/components/expenseMap/TransactionDetailDrawer";
import SiGuDongSelector from "@/components/expenseMap/SiGuDongSelector";
import CurrentLocationBtn from "@/components/expenseMap/CurrentLocationBtn";

type Props = {
  searchParams: {
    keyword: string;
    categoryId: number | null;
    startDate: number | null;
    endDate: number | null;
  };
};

const MapView = ({ searchParams }: Props) => {
  const [curLatitude, setCurLatitude] = useState<number>(37.5665);
  const [curLongitude, setCurLongitude] = useState<number>(126.978);
  const [locationKey, setLocationKey] = useState<number>(0);
  const [lastZoomLevel, setLastZoomLevel] = useState<number | null>(null);
  const [isMapLoaded, setIsMapLoaded] = useState<boolean>(false);
  const [isClicked, setIsClicked] = useState<boolean>(false);
  const [selectedDrawerMarker, setSelectedDrawerMarker] = useState<{
    geohashCode: string;
    id: string;
    address?: string;
  } | null>(null);
  const mapRef = useRef<naver.maps.Map | null>(null); // Naver 지도 인스턴스 (naver.maps.Map) 저장
  const searchParamsRef = useRef(searchParams);
  const markersRef = useRef<naver.maps.Marker[]>([]);
  const svgString = ReactDOMServer.renderToStaticMarkup(<FootPrintIcon />);
  const [selectedSido, setSelectedSido] = useState<number | null>(null);
  const [selectedGugun, setSelectedGugun] = useState<number | null>(null);
  const [selectedDong, setSelectedDong] = useState<number | null>(null);
  const { moveMapByAddress } = useMoveMapByLocation();

  // 지도 위치 이동
  useEffect(() => {
    moveMapByAddress(selectedSido, selectedGugun, selectedDong, mapRef.current, (lat, lng) => {
      setCurLatitude(lat);
      setCurLongitude(lng);
    });
  }, [selectedSido, selectedGugun, selectedDong]);
  const fetchMarkers = async (params = searchParams) => {
    if (!mapRef.current) return;

    const zoomLevel = mapRef.current.getZoom();
    const center = mapRef.current.getCenter();

    try {
      const data = await getClusterMarkers({
        lat: center.lat(),
        lng: center.lng(),
        zoomLevel,
        keyword: params.keyword || null,
        categoryId: params.categoryId ?? null,
        startDate: params.startDate,
        endDate: params.endDate,
      });

      // 기존 마커 제거
      markersRef.current.forEach((m) => m.setMap(null));
      markersRef.current = [];

      const newMarkers = data.map((marker: any) => {
        const size = Math.min(100, 40 + marker.count / 10);
        const markerHtml = `
          <div class="custom-marker" style="position: relative; width: ${size}px; height: ${size}px; transition: transform 0.2s;">
            <div style="width: 100%; height: 100%;">
              ${svgString}
            </div>
            <div style="
              position: absolute;
              top: 18%;
              left: 3%;
              width: 100%;
              height: 100%;
              display: flex;
              align-items: center;
              justify-content: center;
              font-weight: bold;
              color: white;
              font-size: ${Math.max(12, size / 4)}px;
              text-shadow: 0 0 3px rgba(0,0,0,0.6);
              pointer-events: none;
            ">
              ${marker.count}
            </div>
          </div>
        `;

        const markerInstance = new naver.maps.Marker({
          position: new naver.maps.LatLng(marker.latitude, marker.longitude),
          icon: {
            content: markerHtml,
            size: new naver.maps.Size(size, size),
            anchor: new naver.maps.Point(size / 2, size / 2),
          },
          map: mapRef.current!,
        });

        naver.maps.Event.addListener(markerInstance, "click", () => {
          const map = mapRef.current;
          const { latitude, longitude, geohashCode } = marker;

          if (map && map.getZoom() >= 5) {
            const triggerId = `marker-drawer-${geohashCode}-${Date.now()}`;

            naver.maps.Service.reverseGeocode(
              {
                coords: new naver.maps.LatLng(latitude, longitude),
                orders: [naver.maps.Service.OrderType.ROAD_ADDR, naver.maps.Service.OrderType.ADDR],
              },
              (status, response) => {
                if (status !== naver.maps.Service.Status.OK) {
                  return;
                }

                const address =
                  response.v2.address.roadAddress || response.v2.address.jibunAddress || "";

                setSelectedDrawerMarker({
                  geohashCode,
                  id: triggerId,
                  address,
                });

                setTimeout(() => {
                  const el = document.getElementById(triggerId);
                  el?.click();
                }, 0);
              },
            );
          } else {
            const currentZoom = map.getZoom();
            const targetLatLng = new naver.maps.LatLng(latitude, longitude);
            map.setZoom(currentZoom + 1);
            map.setCenter(targetLatLng);
          }
        });

        return markerInstance;
      });

      markersRef.current = newMarkers;
    } catch (error) {
      // console.error("클러스터 마커 가져오기 실패:", error);
    }
  };

  useEffect(() => {
    const style = document.createElement("style");
    style.innerHTML = `
      @keyframes pulse {
        0% { transform: scale(1); }
        50% { transform: scale(1.1); }
        100% { transform: scale(1); }
      }
  
      .cluster-icon:hover .cluster-count {
        transform: scale(1.2);
      }
  
      .custom-marker:hover {
        transform: scale(1.1);
        cursor: pointer;
      }
    `;
    document.head.appendChild(style);
  }, []);

  // 검색 조건 바뀌었을 때 마커 다시 불러오기
  useEffect(() => {
    if (isMapLoaded) {
      fetchMarkers(searchParams);
    }
  }, [searchParams]);

  useEffect(() => {
    searchParamsRef.current = searchParams;
  }, [searchParams]);

  const handleLocationUpdate = (lat: number, lng: number) => {
    setCurLatitude(lat);
    setCurLongitude(lng);
  };

  useEffect(() => {
    const mapElement = document.getElementById("map");
    if (!mapElement) return;

    if (!mapRef.current) {
      mapRef.current = new naver.maps.Map(mapElement, {
        center: new naver.maps.LatLng(curLatitude, curLongitude),
        zoom: 15,
        zoomControl: true,
        zoomControlOptions: {
          style: naver.maps.ZoomControlStyle.SMALL,
          position: naver.maps.Position.TOP_RIGHT,
        },
      });

      mapRef.current.addListener("zoom_changed", () => {
        setIsClicked(false);
      });

      mapRef.current.addListener("idle", () => {
        const currentZoom = mapRef.current?.getZoom();

        // 줌 레벨이 바뀐 경우에만 fetchMarkers 호출
        if (currentZoom !== lastZoomLevel) {
          setLastZoomLevel(currentZoom ?? null);
          fetchMarkers(searchParamsRef.current);
        }
      });

      mapRef.current.addListener("dragstart", () => {
        setIsClicked(false);
      });

      setIsMapLoaded(true);
    }

    fetchMarkers(searchParams);
  }, []);

  useEffect(() => {
    const observer = new MutationObserver(() => {
      const targetDivs = document.querySelectorAll("div");
      targetDivs.forEach((div) => {
        const style = div.getAttribute("style");
        if (
          style?.includes("width: 28px") &&
          style.includes("border: 1px solid rgb(68, 68, 68)") &&
          style.includes("z-index: 0")
        ) {
          div.style.display = "none";
        }
      });
    });

    observer.observe(document.body, {
      childList: true,
      subtree: true,
    });

    return () => observer.disconnect();
  }, []);

  return (
    <div className="relative w-full" style={{ height: "calc(100vh - 7.5rem)" }}>
      <div id="map" className="absolute top-0 left-0 w-full h-full z-0" />
      {isMapLoaded && mapRef.current && (
        <CurrentLocation
          key={locationKey}
          map={mapRef.current}
          onUpdatePosition={handleLocationUpdate}
        />
      )}
      {/* <button
        className="absolute bottom-16 left-5 z-10 bg-white text-primary-500 border border-primary-500 shadow-xl w-12 h-12 rounded-full flex items-center justify-center hover:bg-primary-100 transition"
        onClick={() => {
          setLocationKey((prev) => prev + 1);
          setIsClicked(true);
        }}
        onMouseEnter={() => setIsHovered(true)}
        onMouseLeave={() => setIsHovered(false)}
      >
        <div className="w-7 h-7 flex justify-center items-center">
          {isHovered || isClicked ? <HereMouseAfterIcon /> : <HereMouseBeforeIcon />}
        </div>
      </button> */}
      <CurrentLocationBtn
        onClick={() => {
          setLocationKey((prev) => prev + 1);
          setIsClicked(true);
        }}
      />

      {/* 시군동 선택 드롭다운 */}
      <SiGuDongSelector
        onChange={({ sidoCode, gugunCode, dongCode }) => {
          setSelectedSido(sidoCode);
          setSelectedGugun(gugunCode);
          setSelectedDong(dongCode);
        }}
      />

      {selectedDrawerMarker && (
        <TransactionDetailDrawer
          key={selectedDrawerMarker.id}
          trigger={<div id={selectedDrawerMarker.id} className="hidden" />}
          geohashCode={selectedDrawerMarker.geohashCode}
          address={selectedDrawerMarker.address}
          keyword={searchParams.keyword}
          categoryId={searchParams.categoryId ?? null}
          startDate={searchParams.startDate ?? null}
          endDate={searchParams.endDate ?? null}
        />
      )}
    </div>
  );
};

export default MapView;
