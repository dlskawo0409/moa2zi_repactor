package com.ssafy.moa2zi.account.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("SELECT a.accountNo FROM Account a WHERE a.memberId = :memberId")
    Set<String> findAccountNoById(@Param("memberId") Long memberId);

    List<Account> findByMemberId(Long memberId);
}
