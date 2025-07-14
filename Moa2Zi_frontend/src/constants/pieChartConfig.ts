export interface PieChartConfig {
  [key: string]: {
    label: string;
    color: string;
  };
}

export const pieChartConfig: PieChartConfig = {
  식비: {
    label: "식비",
    color: "#81C1FD",
  },
  "카페/간식": {
    label: "카페/간식",
    color: "#AFE5D0",
  },
  "술/유흥": {
    label: "술/유흥",
    color: "#FAAFBC",
  },
  생활: {
    label: "생활",
    color: "#FFC372",
  },
  온라인쇼핑: {
    label: "온라인쇼핑",
    color: "#C8B6FF",
  },
  "패션/쇼핑": {
    label: "패션/쇼핑",
    color: "#FFD6D6",
  },
  "뷰티/미용": {
    label: "뷰티/미용",
    color: "#B5EAEA",
  },
  교통: {
    label: "교통",
    color: "#A0E7E5",
  },
  자동차: {
    label: "자동차",
    color: "#FFF5BA",
  },
  "주거/통신": {
    label: "주거/통신",
    color: "#FFB5A7",
  },
  "의료/건강": {
    label: "의료/건강",
    color: "#DCC6E0",
  },
  금융: {
    label: "금융",
    color: "#E0BBE4",
  },
  "문화/여가": {
    label: "문화/여가",
    color: "#FDFD96",
  },
  "여행/숙박": {
    label: "여행/숙박",
    color: "#CBF1C1",
  },
  "교육/학습": {
    label: "교육/학습",
    color: "#FFE0B5",
  },
  반려동물: {
    label: "반려동물",
    color: "#AEEEEE",
  },
  "경조/선물": {
    label: "경조/선물",
    color: "#DCEFFF",
  },
};

export const defaultColor = "#D3D3D3";
