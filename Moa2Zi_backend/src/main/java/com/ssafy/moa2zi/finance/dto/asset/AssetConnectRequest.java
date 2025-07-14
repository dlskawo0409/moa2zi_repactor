package com.ssafy.moa2zi.finance.dto.asset;

import java.util.List;

public record AssetConnectRequest(
        List<String> bankCodeList,
        List<String> cardIssuerCodeList
) {
}
