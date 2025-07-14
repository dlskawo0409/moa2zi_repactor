export interface Terms {
  termId: number;
  title: string;
  subTitle: string;
  termType: string;
}

export interface TermDetails {
  termDetailId: number;
  termId: number;
  title: string;
  content: string;
}

export interface TermInfo {
  title: string;
  subTitle: string;
  termDetailList: TermDetails[];
}
