package com.ssafy.moa2zi.challenge.infrastructure;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum ChallengeFormatType {

    // 1. X일 동안 하루 Y원 이하
    DAILY_BUDGET(1, "X일 동안 하루 Y원 이하로 살기 챌린지",
            7, 30, // 기간 범위
            List.of(10000, 15000, 20000, 30000),
            null),

    // 2. X일 동안 총 Y원 이하
    TOTAL_BUDGET(2, "X일 동안 총 Y원 이하로 살기 챌린지",
            7, 30,
            null, // 금액은 X * m 이므로 직접 계산
            "Y = X * m (m: 하루 사용량 10,000~30,000)"),

    // 3. X일 동안 Y 카테고리에 하루 Z원 이하
    CATEGORY_DAILY_ZERO(3, "X일 동안 Y 카테고리에 하루 Z원 이하로 살기 챌린지",
            7, 20,
            null,
            "카테고리 필수, 위에서 제시된 카테고리 중 하나, Z값은 해당 카테고리의 하루 평균 소비량을 꼭 반영해"),

    // 4. X일 동안 Y 카테고리에 총 Z원 이하
    CATEGORY_TOTAL_BUDGET(4, "X일 동안 Y 카테고리에 총 Z원 이하로 살기 챌린지",
            7, 30,
            null,
            "카테고리 필수, Z = X * (카테고리의 지출 평균값)"),

    // 5. X일 동안 Y 카테고리에 Z번 이하만 쓰기
    CATEGORY_LIMIT_COUNT(5, "X일 동안 Y 카테고리에 Z번 이하만 쓰기 챌린지",
            7, 30,
            null,
            "카테고리 필수, Z = X ≤ 7 이면 X의 절반, X ≥ 10 이면 X의 ⅔"),

    // 6. 지난달보다 N% 덜 쓰기
    REDUCE_SPENDING(6, "지난 달보다 N% 덜 쓰기 챌린지",
            30, 30,
            null,
            "N: 10~40%, title 에 카테고리는 넣지마");

    private final int typeNum;
    private final String format;
    private final Integer minPeriod;
    private final Integer maxPeriod;
    private final List<Integer> validAmounts;
    private final String hint;

    // 번호에 해당하는 enum 타입 가져오기
    public static Optional<ChallengeFormatType> fromTypeNum(int typeNum) {
        return Arrays.stream(values())
                .filter(type -> type.typeNum == typeNum)
                .findFirst();
    }
}
