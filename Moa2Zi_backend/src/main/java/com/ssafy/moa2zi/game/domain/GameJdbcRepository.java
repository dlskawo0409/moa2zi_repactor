package com.ssafy.moa2zi.game.domain;

import java.util.List;

public interface GameJdbcRepository {
    void bulkInsert(List<Game> gameList);
}
