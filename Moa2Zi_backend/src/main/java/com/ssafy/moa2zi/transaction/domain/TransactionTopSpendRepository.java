package com.ssafy.moa2zi.transaction.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionTopSpendRepository extends JpaRepository<TransactionTopSpend, Long> {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM TransactionTopSpend")
    void deleteAll();

    Optional<TransactionTopSpend> findByMemberIdAndId(Long memberId, Long topSpendId);
}
