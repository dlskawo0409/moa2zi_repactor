package com.ssafy.moa2zi.transaction.domain;

import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import com.ssafy.moa2zi.transaction.dto.request.MapClusterRequest;
import com.ssafy.moa2zi.transaction.dto.response.MapClusterResponse;

import java.util.List;

public interface TransactionJdbcRepository {

    List<MapClusterResponse> findClustersByMapSearch(
            MapClusterRequest request,
            CustomMemberDetails loginMember
    );

    void bulkInsertTopSpend(List<TransactionTopSpend> topSpendList);
    void bulkInsertTransaction(List<Transaction> transactionList);
}
