import { useState } from "react";
import MapAlertBar from "@components/expenseMap/MapAlertBar";
import MapView from "@components/expenseMap/MapView";
import SearchContainer from "@components/expenseMap/SearchContainer";

const MapPage = () => {
  const [searchParams, setSearchParams] = useState({
    keyword: "",
    categoryId: null as number | null,
    startDate: "",
    endDate: "",
  });
  return (
    <div className="relative w-full h-full">
      {/* <MapAlertBar /> */}
      <SearchContainer setSearchParams={setSearchParams} />
      <MapView searchParams={searchParams} />
    </div>
  );
};

export default MapPage;
