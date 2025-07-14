package com.ssafy.moa2zi.notification.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EmitterRepository {

    private final Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>(); // 메모리 캐시

    public SseEmitter save(String emitterId, SseEmitter emitter) {
        emitterMap.put(emitterId, emitter);
        return emitter;
    }

    public void delete(String emitterId) {
        emitterMap.remove(emitterId);
    }


    public void saveToEventCache(String emitterId, Object notification) {
        eventCache.put(emitterId, notification);
    }


    public Map<String, SseEmitter> findAllEmitterStartWithById(Long memberId) {
        return emitterMap.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(memberId.toString()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


    public Map<String, Object> findAllEventCacheStartWithById(Long memberId) {
        return eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(memberId.toString()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, SseEmitter> findAll() {
        return new HashMap<>(emitterMap);
    }

    public void deleteEventCache(String emitterId) {
        eventCache.remove(emitterId);
    }

    public Map<String, Object> findAllEventCache() {
        return new HashMap<>(eventCache);
    }

    public void cleanOldEventInCache() {
        long now = System.currentTimeMillis();
        eventCache.entrySet().removeIf(entry -> {
            String emitterId = entry.getKey();
            String[] parts = emitterId.split("_");
            if (parts.length < 2) return true; // 잘못된 키 형식이면 제거

            try {
                long timestamp = Long.parseLong(parts[1]);
                return now - timestamp > 5 * 60 * 1000; // 5분 지난 캐시 데이터는 삭제
            } catch (NumberFormatException e) {
                return true;
            }
        });
    }

}
