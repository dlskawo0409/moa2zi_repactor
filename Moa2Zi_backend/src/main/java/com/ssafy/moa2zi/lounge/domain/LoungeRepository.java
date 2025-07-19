package com.ssafy.moa2zi.lounge.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoungeRepository extends JpaRepository<Lounge, Long>, LoungeRepositoryCustom {
}
