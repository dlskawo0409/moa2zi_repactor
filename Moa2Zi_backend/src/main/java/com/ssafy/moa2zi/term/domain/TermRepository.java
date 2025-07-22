package com.ssafy.moa2zi.term.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TermRepository extends JpaRepository<Term, Long> {
    List<Term> findAll();
}
