import { SVGProps } from "react";

export interface Bank {
  bankCode: string;
  bankName: string;
  Icon: (props: SVGProps<SVGSVGElement>) => JSX.Element;
}

export interface CardcardIssuer {
  cardIssuerCode: string;
  cardIssuerName: string;
  Icon: (props: SVGProps<SVGSVGElement>) => JSX.Element;
}
