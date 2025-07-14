import apiClient from "@/services/http";

// 클러스터 마커
export const getClusterMarkers = async ({
  lat,
  lng,
  zoomLevel,
  keyword,
  categoryId,
  startDate,
  endDate,
}: {
  lat: number;
  lng: number;
  zoomLevel: number;
  keyword?: string | null;
  categoryId?: number | null;
  startDate: string;
  endDate: string;
}) => {
  const params = {
    lat,
    lng,
    zoomLevel,
    keyword,
    categoryId,
    startDate,
    endDate,
  };

  const { data } = await apiClient.get("/transactions/map/clusters", {
    params,
  });

  return data;
};

export const getTransactionMarkers = async ({
  keyword,
  categoryId,
  startDate,
  endDate,
  geohashCode,
  next = 0,
  size = 100,
}: {
  keyword: string;
  categoryId: number;
  startDate: number;
  endDate: number;
  geohashCode: string;
  next?: number;
  size?: number;
}) => {
  const params = {
    keyword,
    categoryId,
    startDate,
    endDate,
    geohashCode,
    next,
    size,
  };

  const { data } = await apiClient.get("/transactions/map", {
    params,
  });

  return data;
};

// 위치 기반 알림 API 호출
export const getTransactionAlerts = async ({
  latitude,
  longitude,
}: {
  latitude: number;
  longitude: number;
}) => {
  const { data } = await apiClient.get("/transactions/location/alert", {
    params: { latitude, longitude },
  });

  return data; // 알림 배열
};

// 시/도 리스트 가져오기
export const getSidoList = async () => {
  const { data } = await apiClient.get("/geo/sido", {});
  return data; // [{ sidoCode, sidoName }]
};

// 구/군 리스트 가져오기
export const getGugunList = async (sidoCode: number) => {
  const { data } = await apiClient.get("/geo/gugun", {
    params: { sidoCode },
  });
  return data; // [{ gugunCode, gugunName }]
};

// 동 리스트 가져오기
export const getDongList = async (gugunCode: number) => {
  const { data } = await apiClient.get("/geo/dong", {
    params: { gugunCode },
  });
  return data; // [{ dongCode, dongName }]
};

export const getLocationByCodes = async (address: string) => {
  try {
    return new Promise((resolve, reject) => {
      naver.maps.Service.geocode({ query: address }, (status, response) => {
        const addresses = response?.v2?.addresses;

        if (
          status === naver.maps.Service.Status.OK &&
          Array.isArray(addresses) &&
          addresses.length > 0
        ) {
          const location = addresses[0];
          resolve({
            latitude: parseFloat(location.y),
            longitude: parseFloat(location.x),
          });
        } else {
          reject(new Error("위치 정보를 찾을 수 없습니다."));
        }
      });
    });
  } catch (error) {
    // console.error("위치 정보를 가져오는 데 실패했습니다.", error);
    throw error;
  }
};
