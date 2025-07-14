import { useEffect } from "react";

interface Props {
  map: naver.maps.Map | null;
  onUpdatePosition?: (lat: number, lng: number) => void;
}

const currentMarker: { marker?: naver.maps.Marker; circle?: naver.maps.Circle } = {};

const CurrentLocation = ({ map, onUpdatePosition }: Props) => {
  useEffect(() => {
    if (!map) return;

    navigator.geolocation.getCurrentPosition(
      (position) => {
        const { latitude, longitude } = position.coords;
        const newPosition = new naver.maps.LatLng(latitude, longitude);

        onUpdatePosition?.(latitude, longitude);

        if (!currentMarker.marker) {
          currentMarker.marker = new naver.maps.Marker({
            position: newPosition,
            map,
            title: "현재 위치",
            icon: {
              content:
                '<div style="background-color: red; width: 10px; height: 10px; border-radius: 50%; z-index:999; position:absolute"></div>',
              size: new naver.maps.Size(10, 10),
              anchor: new naver.maps.Point(5, 5),
            },
          });
        } else {
          currentMarker.marker.setPosition(newPosition);
        }

        // 200m 원 그리기
        if (currentMarker.circle) {
          currentMarker.circle.setMap(null);
        }
        currentMarker.circle = new naver.maps.Circle({
          map,
          center: newPosition,
          radius: 200,
          strokeColor: "#FFAA00",
          strokeOpacity: 0.2,
          strokeWeight: 2,
          fillColor: "#FFA500",
          fillOpacity: 0.2,
        });

        map.setCenter(newPosition);
        map.setZoom(17);
      },
      () => {
        alert("위치 정보를 불러오지 못했어요. 잠시후 다시 시도해 주세요!");
      },
      {
        enableHighAccuracy: false,
        timeout: 3000,
        maximumAge: 10000,
      },
    );
  }, [map]);

  return null;
};

export default CurrentLocation;
