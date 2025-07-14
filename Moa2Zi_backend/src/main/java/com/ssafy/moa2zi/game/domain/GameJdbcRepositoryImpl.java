package com.ssafy.moa2zi.game.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@RequiredArgsConstructor
public class GameJdbcRepositoryImpl implements GameJdbcRepository{

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void bulkInsert(List<Game> gameList) {
        String sql = "INSERT INTO games (lounge_id, created_at, end_time) VALUES (? , ? , ?)";


        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {

                Game now = gameList.get(i);

                ps.setLong(1, now.getLoungeId());
                ps.setTimestamp(2, Timestamp.valueOf(now.getCreatedAt()));
                ps.setTimestamp(3, Timestamp.valueOf(now.getEndTime()));

            }

            @Override
            public int getBatchSize() {
                return gameList.size();
            }
        });
    }
}
