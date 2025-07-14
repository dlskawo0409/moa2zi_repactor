package com.ssafy.moa2zi.answer.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

	boolean existsByQuizIdAndMemberId(Long quizId, Long memberId);
}
