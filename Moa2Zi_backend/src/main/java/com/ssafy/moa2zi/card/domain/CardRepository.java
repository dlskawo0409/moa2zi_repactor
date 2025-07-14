package com.ssafy.moa2zi.card.domain;

import com.ssafy.moa2zi.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Set;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    @Query("SELECT c.cardNo FROM Card c WHERE c.memberId = :memberId")
    Set<String> findCardNoByMemberId(@Param("memberId") Long memberId);

    List<Card> findByMemberId(Long memberId);
}
