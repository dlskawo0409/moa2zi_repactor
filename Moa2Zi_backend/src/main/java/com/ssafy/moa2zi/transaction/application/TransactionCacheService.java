package com.ssafy.moa2zi.transaction.application;

import com.ssafy.moa2zi.transaction.domain.TransactionTopSpend;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionCacheService {

    private static final String TOP_SPEND_GEO_KEY = "top5:spend:";
    private static final String TOP_SPEND_TTL_KEY = "top5:ttl:";

    private static final String MEMBER_HISTORY_KEY = "history:search:";
    private static final String MEMBER_HISTORY_TTL_KEY = "history:ttl:";

    private final StringRedisTemplate redisTemplate;

    /**
     * 각 유저 별 top5 소비 내역의 Geospatial 정보로 레디스에 저장
     * key : [top5:spend:{memberId}]
     */
    public void cacheTop5Transactions(List<TransactionTopSpend> topSpends) {

        for(TransactionTopSpend topSpend : topSpends) {

            Point point = new Point(topSpend.getCoordinate().getX(), topSpend.getCoordinate().getY());
            String geoKey = generateTop5GeoKey(topSpend.getMemberId());

            // GeoHash 값으로 인코딩하여 좌표 저장
            redisTemplate.opsForGeo().add(geoKey, point, String.valueOf(topSpend.getId()));
            redisTemplate.expire(geoKey, Duration.ofDays(1));
        }
    }

    public void clearTop5Transactions(Set<Long> memberIds) {
        for(Long memberId : memberIds) {
            String geoKey = generateTop5GeoKey(memberId);
            redisTemplate.delete(geoKey);
        }
    }

    /**
     * 현재 클라이언트의 (위도, 경도) 좌표로 주변 top5 소비 내역이 존재하는지 확인
     */
    public List<Long> geoHashTop5SearchWithinRadius(float latitude, float longitude, Long memberId) {

        String geoKey = generateTop5GeoKey(memberId);
        Distance radius = new Distance(300, RedisGeoCommands.DistanceUnit.METERS);
        Circle searchArea = new Circle(new Point(longitude, latitude), radius);

        // 반경 내 top5 소비 내역 검색
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo().search(geoKey, searchArea);

        if (results == null || results.getContent().isEmpty()) {
            return List.of();
        }

        // 검색된 TOP 5 소비내역 ID 반환
        return results.getContent().stream()
                .map(result -> result.getContent().getName())
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }

    /**
     * 중복 알림을 제거하기 위해 알림을 보낸 top 5 소비내역을 캐싱
     * 이틀 TTL
     */
    public void cacheTop5Alert(Long memberId, Long topSpendId) {
        String alertKey = generateTop5TtlKey(memberId, topSpendId);
        redisTemplate.opsForValue().set(alertKey, "ok", Duration.ofDays(2));
    }

    public boolean isAlreadyTop5Alerted(Long memberId, Long topSpendId) {
        String alertKey = generateTop5TtlKey(memberId, topSpendId);
        return redisTemplate.hasKey(alertKey);
    }

    /**
     * 클라이언트의 검색 요청 캐싱
     * 12시간동안 비슷한 위치에서의 중복 요청을 막음
     */
    public void cacheRequestFromClient(float latitude, float longitude, Long memberId) {
        Point point = new Point(longitude, latitude);
        String member = point.getY() + ":" + point.getX();

        String searchKey = generateHistoryGeoKey(memberId);
        String ttlKey = generateHistoryTtlKey(member, memberId);

        // 클라이언트로부터 요청된 좌표 Geo 정보 저장, TTL 12시간 적용
        redisTemplate.opsForGeo().add(searchKey, point, member);
        redisTemplate.opsForValue().set(ttlKey, "ok", Duration.ofHours(6));
        redisTemplate.expire(ttlKey, Duration.ofHours(12));
    }

    // 100 미터 내 요청된 기록이 있는 지 여부 확인
    public List<String> geoHashHistorySearchWithinRadius(float latitude, float longitude, Long memberId) {
        String historyGeoKey = generateHistoryGeoKey(memberId);
        GeoReference<String> reference = GeoReference.fromCoordinate(new Point(longitude, latitude));
        Distance radius = new Distance(100, RedisGeoCommands.DistanceUnit.METERS);

        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs
                .newGeoRadiusArgs()
                .includeCoordinates();

        // 100 미터 반경 내 요청 기록이 있는지 확인
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo()
                .search(historyGeoKey, reference, radius, args);

        if (results == null || results.getContent().isEmpty()) {
            return List.of();
        }

        return results.getContent().stream()
                .map(result -> result.getContent().getName())
                .collect(Collectors.toList());
    }

    public boolean hasRequestNearBy(float latitude, float longitude, Long memberId) {
        List<String> historySearchList = geoHashHistorySearchWithinRadius(latitude, longitude, memberId);

        // 검색된 히스토리 좌표의 TTL 확인
        for(String reference : historySearchList) {
            String historyTtlKey = generateHistoryTtlKey(reference, memberId);
            if(redisTemplate.hasKey(historyTtlKey)) {
                return true;
            }
        }

        return false;
    }


    private String generateTop5GeoKey(Long memberId) {
        return TOP_SPEND_GEO_KEY + memberId;
    }

    private String generateTop5TtlKey(Long memberId, Long topSpendId) {
        return TOP_SPEND_TTL_KEY + memberId + ":" + topSpendId;
    }

    private String generateHistoryGeoKey(Long memberId) {
        return MEMBER_HISTORY_KEY + memberId;
    }

    private String generateHistoryTtlKey(String reference, Long memberId) {
        return MEMBER_HISTORY_TTL_KEY + memberId + ":" + reference;
    }

}
