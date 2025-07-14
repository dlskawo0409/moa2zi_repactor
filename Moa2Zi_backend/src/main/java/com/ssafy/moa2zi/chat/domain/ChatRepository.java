package com.ssafy.moa2zi.chat.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ChatRepository extends MongoRepository<Chat, String>, ChatRepositoryCustom{

    List<Chat> findByLoungeIdAndTimeStampLessThanOrderByTimeStampDesc(Long loungeId, LocalDateTime lastTimestamp, Pageable pageable);
    List<Chat> findByLoungeIdOrderByTimeStampDesc(Long loungeId, Pageable pageable);

    Long countByLoungeId(Long loungeId);
    List<Chat> findByLoungeId(Long loungeId);
    Long countByLoungeIdAndTimeStampAfter(Long loungeId, LocalDateTime createdAt);
    Chat findTopByLoungeIdOrderByTimeStampDesc(Long loungeId);
}
