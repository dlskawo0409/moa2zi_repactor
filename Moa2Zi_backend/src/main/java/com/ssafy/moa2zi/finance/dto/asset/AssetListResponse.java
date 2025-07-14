package com.ssafy.moa2zi.finance.dto.asset;

import java.util.List;

public record AssetListResponse(
        List<BankInfoResponse> bankList,
        List<CardIssuerInfoResponse> cardIssuerList
) {

    public static AssetListResponse from(
            List<BankInfoResponse> bankList,
            List<CardIssuerInfoResponse> cardIssuerList
    ) {

        return new AssetListResponse(
                bankList,
                cardIssuerList
        );
    }
}
