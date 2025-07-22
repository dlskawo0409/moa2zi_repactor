package com.ssafy.moa2zi.term.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TermDetailRepository extends JpaRepository<TermDetail, Long> {
    List<TermDetail> findByTermId(Long termId);
}
