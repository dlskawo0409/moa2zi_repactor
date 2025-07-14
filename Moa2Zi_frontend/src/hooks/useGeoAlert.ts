import { useEffect, useRef, useState } from "react";
import { getTransactionAlerts } from "@/services/map";

export const useGeoAlert = () => {
  const lastLoggedPositionRef = useRef<{ lat: number; lng: number } | null>(null);
  const [alertContent, setAlertContent] = useState<string>("");
  const [showAlert, setShowAlert] = useState<boolean>(false);

  useEffect(() => {
    const getDistanceFromLatLonInMeters = (
      lat1: number,
      lon1: number,
      lat2: number,
      lon2: number,
    ) => {
      const R = 6371e3;
      const toRad = (x: number) => (x * Math.PI) / 180;

      const dLat = toRad(lat2 - lat1);
      const dLon = toRad(lon2 - lon1);
      const a =
        Math.sin(dLat / 2) ** 2 +
        Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) * Math.sin(dLon / 2) ** 2;
      const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
      return R * c;
    };

    const intervalId = setInterval(() => {
      navigator.geolocation.getCurrentPosition(
        async (position) => {
          const { latitude, longitude } = position.coords;

          if (!lastLoggedPositionRef.current) {
            lastLoggedPositionRef.current = { lat: latitude, lng: longitude };
            return;
          }

          const distance = getDistanceFromLatLonInMeters(
            lastLoggedPositionRef.current.lat,
            lastLoggedPositionRef.current.lng,
            latitude,
            longitude,
          );

          if (distance >= 100) {
            lastLoggedPositionRef.current = { lat: latitude, lng: longitude };
            // alert("100m 이동");

            try {
              const alerts = await getTransactionAlerts({ latitude, longitude });
              if (alerts.length > 0) {
                setAlertContent(alerts[0].content || "이 근처에서 과소비한 적이 있어요!");
                setShowAlert(true);
                setTimeout(() => setShowAlert(false), 5000);
              }
            } catch (err) {
              // console.error("위치 기반 알림 실패:", err);
            }
          }
        },
        (err) => {
          // console.error("위치 정보 실패:", err);
        },
        {
          enableHighAccuracy: true,
          maximumAge: 30000,
          timeout: 5000,
        },
      );
    }, 15000);

    return () => clearInterval(intervalId);
  }, []);

  return { showAlert, alertContent, setAlertContent, setShowAlert };
};
