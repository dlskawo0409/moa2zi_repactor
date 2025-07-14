package com.ssafy.moa2zi.chat.domain;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;

import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
@Repository
@RequiredArgsConstructor
public class ChatRepositoryCustomImpl implements ChatRepositoryCustom {

	private final MongoTemplate mongoTemplate;

	@Override
	public Map<Long, LocalDateTime> findLatestChatTimeByLoungeIds(List<Long> loungeIds) {
		MatchOperation match = Aggregation.match(Criteria.where("loungeId").in(loungeIds));
		GroupOperation group = Aggregation.group("loungeId")
			.max("timeStamp").as("latestChatTime");

		Aggregation aggregation = Aggregation.newAggregation(match, group);

		AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "chat", Document.class);

		Map<Long, LocalDateTime> latestMap = new HashMap<>();
		for (Document doc : results.getMappedResults()) {
			Long loungeId = doc.getLong("_id");
			Date latest = doc.getDate("latestChatTime");
			if (loungeId != null && latest != null) {
				latestMap.put(loungeId, latest.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
			}
		}

		return latestMap;
	}


}