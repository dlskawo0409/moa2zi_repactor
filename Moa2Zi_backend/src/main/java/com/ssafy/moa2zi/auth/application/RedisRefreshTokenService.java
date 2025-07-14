package com.ssafy.moa2zi.auth.application;

import org.springframework.stereotype.Service;

import com.ssafy.moa2zi.auth.domain.RefreshTokenRepository;
import com.ssafy.moa2zi.auth.exception.AuthException;
import com.ssafy.moa2zi.auth.jwt.JWTUtil;
import com.ssafy.moa2zi.member.domain.Member;
import com.ssafy.moa2zi.member.domain.MemberRepository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisRefreshTokenService {

	private final RefreshTokenRepository refreshTokenRepository;
	private final JWTUtil jwtUtil;
	private final MemberRepository memberRepository;


	public Member getMemberByRefreshToken(final String refreshToken) throws
		AuthException.AuthBadRequestException,
		MemberException.MemberBadRequestException {
		RefreshToken refreshToken1 = refreshTokenRepository.findById(refreshToken)
			.orElseThrow(() -> new AuthException.AuthBadRequestException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUNDED));

		System.out.println(refreshToken1);


		return memberRepository.findById(refreshToken1.getMemberId())
			.orElseThrow(() -> new MemberException.MemberBadRequestException(MemberErrorCode.MEMBER_NOT_FOUND));
	}

	public String generateRefreshToken(Long memberId){
		String refreshToken = UUID.randomUUID().toString();
		RefreshToken redis = RefreshToken.builder()
			.refreshToken(refreshToken)
			.memberId(memberId)
			.build();
		log.info("userDetails.getUser().getId() = {}", memberId);
		refreshTokenRepository.save(redis);
		return refreshToken;
	}

	public void deleteRefreshToken(String refreshToken){
		refreshTokenRepository.deleteById(refreshToken);
	}

	public AccessAndRefreshToken getAccessAndRefreshToken(final String refreshToken) throws MemberException.MemberBadRequestException, AuthException.AuthBadRequestException {
		Member member = getMemberByRefreshToken(refreshToken);
		String newAccessToken = jwtUtil.createAccessJwt(member.getUsername(), member.getRole().getKey(), member.getMemberId(), member.getNickname());
		String newRefreshToken = generateRefreshToken(member.getMemberId());

		deleteRefreshToken(refreshToken);

		return AccessAndRefreshToken.builder()
			.accessToken(newAccessToken)
			.refreshToken(newRefreshToken).build();
	}


}