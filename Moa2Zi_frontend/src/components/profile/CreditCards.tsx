import { useEffect, useState } from "react";

import { useUserInfo } from "@/hooks/useUserInfo";

import CreditCard from "@/components/profile/CreditCard";
import CreditCardSkeleton from "@/components/profile/CreditCardSkeleton";
import CreditCardNotFound from "@/components/profile/CreditCardNotFound";
import {
  Carousel,
  CarouselContent,
  CarouselCardItem,
  CarouselPrevious,
  CarouselNext,
} from "@/components/ui/carousel";
import { getMemberCards } from "@/services/finance";
import type { CardInfo } from "@/types/card";

const CreditCards = () => {
  const { data } = useUserInfo();
  const [cards, setCards] = useState<CardInfo[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  useEffect(() => {
    const fetchCards = async () => {
      try {
        if (!data) return;

        const cardData = await getMemberCards(data.memberId);
        setCards(cardData);
      } catch (error) {
        // console.error("카드 불러오기 실패:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchCards();
  }, [data]);

  if (isLoading) {
    return (
      <div className="w-full mx-5">
        <CreditCardSkeleton />
      </div>
    );
  }

  if (cards.length === 0) {
    return (
      <div className="w-full mx-5">
        <CreditCardNotFound />
      </div>
    );
  }

  return (
    <Carousel className="w-full">
      <CarouselContent className="flex mx-5">
        {cards.map((card) => (
          <CarouselCardItem key={card.cardNo} className="flex justify-center">
            <CreditCard
              name={card.cardName}
              code={parseInt(card.cardIssuerCode)}
              number={parseInt(card.cardNo.slice(-4))}
            />
          </CarouselCardItem>
        ))}
      </CarouselContent>
      <CarouselPrevious className="hidden pc:flex ms-20" />
      <CarouselNext className="hidden pc:flex me-20" />
    </Carousel>
  );
};

export default CreditCards;
