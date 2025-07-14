package com.ssafy.moa2zi.member.domain;

import com.ssafy.moa2zi.member.dto.request.MemberGetByNicknameRequest;
import com.ssafy.moa2zi.member.dto.response.MemberGetByNicknameListResponse;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface MemberRepositoryCustom {
    List<Long> getMemberIdListByNickname(String nickname, Long memberId);
    MemberGetByNicknameListResponse getMemberByNickname(
            MemberGetByNicknameRequest memberGetByNicknameRequest,
            Long memberId
    ) throws BadRequestException;
}
