package com.ssafy.moa2zi.challenge.domain;

import java.util.List;

public interface ChallengeRecommendJdbcRepository {

    void bulkInsert(List<ChallengeRecommend> challengeRecommends);
}
