package com.ssafy.moa2zi.challenge.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class ChallengeRecommendJdbcRepositoryImpl implements ChallengeRecommendJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void bulkInsert(List<ChallengeRecommend> challengeRecommends) {
        String sql = "INSERT INTO challenge_recommends (member_id, challenge_time_id, created_at, updated_at) VALUES (?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, challengeRecommends.get(i).getMemberId());
                ps.setLong(2, challengeRecommends.get(i).getChallengeTimeId());
                ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            }

            @Override
            public int getBatchSize() {
                return challengeRecommends.size();
            }
        });

    }

}
