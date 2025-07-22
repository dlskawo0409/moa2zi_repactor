package com.ssafy.moa2zi.day.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DayCommentRepository extends JpaRepository<DayComment, Long>, DayCommentRepositoryCustom {
    boolean existsByIdAndDayId(Long id, Long dayId);
}
