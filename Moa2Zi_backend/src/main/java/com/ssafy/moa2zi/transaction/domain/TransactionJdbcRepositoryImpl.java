package com.ssafy.moa2zi.transaction.domain;

import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import com.ssafy.moa2zi.transaction.dto.request.MapClusterRequest;
import com.ssafy.moa2zi.transaction.dto.response.MapClusterResponse;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class TransactionJdbcRepositoryImpl implements TransactionJdbcRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final JdbcTemplate batchJdbcTemplate;

    /**
     * 지도 화면 내 소비내역
     * 클러스터링 조회
     */
    @Override
    public List<MapClusterResponse> findClustersByMapSearch(
            MapClusterRequest request,
            CustomMemberDetails loginMember
    ) {

        // SQL 에선 X가 위도, Y가 경도
        StringBuilder sql = new StringBuilder(
                """
                    SELECT 
                        AVG(ST_X(t.coordinate)) AS latitude,
                        AVG(ST_Y(t.coordinate)) AS longitude,
                        LEFT(t.geohash_code, :clusterPrecision) AS geohash_code_cluster,
                        COUNT(*) AS count
                    FROM transactions t
                    INNER JOIN days d ON t.day_id = d.day_id
                    WHERE t.member_id = :member_id
                      AND t.geohash_code LIKE CONCAT(
                          ST_GeoHash(:lng, :lat, :basePrecision), '%'
                      )
                """
        );

        int basePrecision = getBasePrecisionByZoomlevel(request);
        int clusterPrecision = getClusterPrecision(request, basePrecision);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("member_id", loginMember.getMemberId())
                .addValue("lat", request.lat())
                .addValue("lng", request.lng())
                .addValue("basePrecision", basePrecision)
                .addValue("clusterPrecision", clusterPrecision);


        if (request.startDate() != null && request.endDate() != null) {
            sql.append(" AND d.transaction_date BETWEEN :startDate AND :endDate");
            params.addValue("startDate", request.startDate());
            params.addValue("endDate", request.endDate());
        }

        if (request.categoryId() != null) {
            sql.append(" AND t.category_id = :categoryId");
            params.addValue("categoryId", request.categoryId());
        }

        if (request.keyword() != null && !request.keyword().isBlank()) {
            sql.append(" AND MATCH(t.merchant_name) AGAINST(:keyword IN BOOLEAN MODE)");
            params.addValue("keyword", request.keyword());
        }

        sql.append(" GROUP BY geohash_code_cluster");

        List<MapClusterResponse> result = jdbcTemplate.query(sql.toString(), params, (rs, rowNum) ->
            new MapClusterResponse(
                    rs.getDouble("latitude"),
                    rs.getDouble("longitude"),
                    rs.getString("geohash_code_cluster"),
                    rs.getInt("count")
            )
        );

        return result;
    }

    @Override
    public void bulkInsertTopSpend(List<TransactionTopSpend> topSpendList) {
        String sql = "INSERT INTO transaction_top_spends " +
                "(transaction_id, member_id, rank_num, merchant_name, coordinate, transaction_balance, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ST_GeomFromText(?, 4326), ?, ?, ?)";

        batchJdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, topSpendList.get(i).getTransactionId());
                ps.setLong(2, topSpendList.get(i).getMemberId());
                ps.setInt(3, topSpendList.get(i).getRankNum());
                ps.setString(4, topSpendList.get(i).getMerchantName());
                ps.setString(5, convertToPointStr(topSpendList.get(i).getCoordinate()));
                ps.setLong(6, topSpendList.get(i).getBalance());
                ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
                ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            }

            @Override
            public int getBatchSize() {
                return topSpendList.size();
            }

        });
    }

    public void bulkInsertTransaction(List<Transaction> transactionList) {
        String sql = "INSERT INTO transactions (" +
                "account_no, transaction_balance, card_no, day_id, member_id, memo, " +
                "merchant_name, payment_method, transaction_time, transaction_type, " +
                "created_at, updated_at, is_in_budget, transaction_unique_no, merchant_id" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        batchJdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, transactionList.get(i).getAccountNo());
                ps.setLong(2, transactionList.get(i).getBalance());
                ps.setString(3, transactionList.get(i).getCardNo());
                ps.setLong(4, transactionList.get(i).getDayId());
                ps.setLong(5, transactionList.get(i).getMemberId());
                ps.setString(6, transactionList.get(i).getMemo());
                ps.setString(7, transactionList.get(i).getMerchantName());
                ps.setString(8, transactionList.get(i).getPaymentMethod());
                ps.setString(9, transactionList.get(i).getTransactionTime());
                ps.setString(10, String.valueOf(transactionList.get(i).getTransactionType()));
                ps.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
                ps.setTimestamp(12, Timestamp.valueOf(LocalDateTime.now()));
                ps.setBoolean(13, true);
                ps.setString(14, transactionList.get(i).getTransactionUniqueNo());
                ps.setLong(15, transactionList.get(i).getMerchantId());
            }

            @Override
            public int getBatchSize() {
                return transactionList.size();
            }

        });
    }

    private String convertToPointStr(Point point) {
        // locationtech.jts.geom 의 POINT 타입의 Y는 위도, X는 경도 주의!!
        return String.format("POINT(%f %f)", point.getY(), point.getX());
    }

    // 프론트랑 얘기해서 zoomlevel 에 따른 정밀도를 같이 결정해야 함
    private int getBasePrecisionByZoomlevel(MapClusterRequest request) {
        int zoomLevel = request.zoomLevel();
        int baseZoom = 15;
        int basePrecision = 4;

        int precision;
        if (zoomLevel >= baseZoom) {
            precision = basePrecision + ((zoomLevel - baseZoom) / 2);
        } else {
            precision = basePrecision - ((baseZoom - zoomLevel) / 2);
        }

        return Math.max(1, Math.min(precision, 6)); // precision 범위 제한 1~6
    }

    public int getClusterPrecision(MapClusterRequest request, int basePrecision){
        int zoomLevel = request.zoomLevel();

        if(zoomLevel == 15 || zoomLevel == 16) {
            return 7;
        } else if(zoomLevel == 17 || zoomLevel == 18) {
            return 8;
        } else if(zoomLevel >= 19) {
            return 9;
        } else
            return basePrecision + 3;
    }

}
