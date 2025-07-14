package com.ssafy.moa2zi.chat.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ChatRepositoryCustom {
	Map<Long, LocalDateTime> findLatestChatTimeByLoungeIds(List<Long> loungeIds);
}
