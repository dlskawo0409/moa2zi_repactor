package com.ssafy.moa2zi.finance.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.moa2zi.finance.dto.asset.AssetConnectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FinanceCacheService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String MEMBER_ASSET_CONNECT = "asset:connect:pending:";
    private static final String MEMBER_SYNC_PROCESS = "sync:process:";
    private static final String MEMBER_AUTH_LIMIT = "auth:limit:";
    private static final String MEMBER_AUTH_RETRY = "auth:retry:";
    private static final String MEMBER_AUTH_LOCK = "auth:lock:";

    /**
     * 사용자가 선택한 은행, 카드사 캐싱
     */
    public void saveAssetSelection(Long memberId, AssetConnectRequest request)  {
        String key = generateAssetConnectKey(memberId);
        try {
            String object  = objectMapper.writeValueAsString(request);
            redisTemplate.opsForValue().set(key, object, Duration.ofMinutes(30));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 선택했던 은행, 카드사 정보 가져오기
     */
    public AssetConnectRequest getAssetSelection(Long memberId) {
        String key = generateAssetConnectKey(memberId);
        String object = redisTemplate.opsForValue().get(key);
        if(Objects.isNull(object)) {
            throw new NotFoundException("해당 ID의 유저가 탐색 대기 중인 자산이 없습니다, " + memberId);
        }

        try {
            return objectMapper.readValue(object, AssetConnectRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 자산 연결 후 캐싱 삭제
     */
    public void deleteAssetSelection(Long memberId) {
        String key = generateAssetConnectKey(memberId);
        String object = redisTemplate.opsForValue().get(key);
        if(Objects.isNull(object)) {
            throw new NotFoundException("해당 ID의 유저가 탐색 대기 중인 자산이 없습니다, " + memberId);
        }

        redisTemplate.delete(key);
    }

    /**
     * 동시에 같은 유저의 동기화 작업 진행을 방지하기 위헤 키 생성
     */
    public boolean isProcessingSyncTask(Long memberId) {
        String key = generateMemberProcessingKey(memberId);
        Boolean set = redisTemplate.opsForValue()
                .setIfAbsent(key, "PROCESSING", Duration.ofMinutes(3)); // key 가 없다면 생성 후 TRUE 반환
        return !Boolean.TRUE.equals(set);
    }

    public void clearProcessing(Long memberId) {
        String key = generateMemberProcessingKey(memberId);
        redisTemplate.delete(key);
    }

    /**
     * 1원 송금 요청 횟수 카운트 (하루 요청 제한 수 5번)
     */
    public void checkAccountAuthLimit(Long memberId) throws IllegalAccessException {
        String authLimitKey = generateMemberAuthLimitKey(memberId);

        // 현재까지의 1원 송금 요청 횟수
        int currentCount = Optional.ofNullable(redisTemplate.opsForValue().get(authLimitKey))
                .map(Integer::parseInt)
                .orElse(0);

        // 24시간 내 5번의 인증 제한 횟수
        if(currentCount >= 5) {
            throw new IllegalAccessException("1원 송금 인증 횟수를 초과했습니다.");
        }

        Long newCount = redisTemplate.opsForValue().increment(authLimitKey);
        if(newCount != null && newCount == 1L) { // 처음 인증 시도 시 24시간 TTL 설정
            redisTemplate.expire(authLimitKey, Duration.ofMinutes(24));
        }
    }

    /**
     * 1원 송금 검증 횟수 카운트 (기회 3번)
     * 시간 제한은 금융망 API 에서 자체적으로 막아놓음
     */
    public void checkAuthRetryCount(Long memberId) throws IllegalAccessException {
        String authRetryKey = generateMemberAuthRetryKey(memberId);

        // 현재까지의 검증 횟수
        int currentCount = Optional.ofNullable(redisTemplate.opsForValue().get(authRetryKey))
                .map(Integer::parseInt)
                .orElse(0);

        // 3번 검증 시도 초과하면 15분 동안 락 생성
        if(currentCount >= 3) {
            String authLockKey = generateMemberAuthLockKey(memberId);
            redisTemplate.opsForValue().set(authLockKey, "LOCK", Duration.ofMinutes(15));
            throw new IllegalAccessException("1원 인증 검증 횟수를 초과했습니다.");
        }

        redisTemplate.opsForValue().increment(authRetryKey);
        redisTemplate.expire(authRetryKey, Duration.ofMinutes(10));
    }

    /**
     * 인증 요청이 막힌 사용자인지 확인
     */
    public boolean isAuthLocked(Long memberId) {
        String authLockKey = generateMemberAuthLockKey(memberId);
        return Boolean.TRUE.equals(redisTemplate.hasKey(authLockKey));
    }

    /**
     * 1원 송금 검증 성공 시 시도 횟수 초기화
     */
    public void clearAuthRetryCount(Long memberId) {
        String authRetryKey = generateMemberAuthRetryKey(memberId);
        redisTemplate.delete(authRetryKey);
    }

    private String generateMemberAuthLockKey(Long memberId) {
        return MEMBER_AUTH_LOCK + memberId;
    }

    private String generateMemberAuthRetryKey(Long memberId) {
        return MEMBER_AUTH_RETRY + memberId;
    }

    private String generateMemberAuthLimitKey(Long memberId) {
        return MEMBER_AUTH_LIMIT + memberId;
    }

    private String generateMemberProcessingKey(Long memberId) {
        return MEMBER_SYNC_PROCESS + memberId;
    }

    private String generateAssetConnectKey(Long memberId) {
        return MEMBER_ASSET_CONNECT + memberId;
    }

}
