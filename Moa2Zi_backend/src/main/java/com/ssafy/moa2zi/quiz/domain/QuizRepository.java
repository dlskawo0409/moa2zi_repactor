package com.ssafy.moa2zi.quiz.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> , QuizRepositoryCustom{

    List<Quiz> findByGameId(Long gameId);

    int countByGameId(Long gameId);

}
