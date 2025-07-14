package com.ssafy.moa2zi.finance.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FinanceEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publish(FinanceEvent event) {
        eventPublisher.publishEvent(event);
        log.info("[FinanceEventPublisher] sending event : {}", event);
    }

}
