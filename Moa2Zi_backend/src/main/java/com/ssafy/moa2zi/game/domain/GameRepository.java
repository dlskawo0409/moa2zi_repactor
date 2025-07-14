package com.ssafy.moa2zi.game.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GameRepository extends JpaRepository<Game, Long>, GameRepositoryCustom, GameJdbcRepository {
	List<Game> findByLoungeId(Long loungeId);

	@Query("SELECT g.id FROM Game g WHERE g.loungeId = :loungeId AND g.createdAt = :createdAt AND g.endTime = :endTime")
	Optional<Long> findGameIdByLoungeIdAndCreatedAtAndEndTime(Long loungeId, LocalDateTime createdAt, LocalDateTime endTime);

}