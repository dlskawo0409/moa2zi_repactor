package com.ssafy.moa2zi.auth.application;

import com.ssafy.moa2zi.auth.domain.RefreshToken;
import com.ssafy.moa2zi.auth.domain.RefreshTokenRepository;
import com.ssafy.moa2zi.auth.domain.TempMemberDTO;
import com.ssafy.moa2zi.auth.domain.TempMemberRepository;
import com.ssafy.moa2zi.auth.dto.response.AccessAndRefreshToken;
import com.ssafy.moa2zi.auth.jwt.JWTUtil;
import com.ssafy.moa2zi.member.domain.Member;
import com.ssafy.moa2zi.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisRefreshTokenService {

	private final RefreshTokenRepository refreshTokenRepository;
	private final TempMemberRepository tempMemberRepository;
	private final JWTUtil jwtUtil;
	private final MemberRepository memberRepository;


	public Member getMemberByRefreshToken(final String refreshToken){
		RefreshToken checkedRefreshToken = findRefreshTokenByRefreshToken(refreshToken);

		return findMemberByRefreshToken(checkedRefreshToken.getMemberId());
	}

	private RefreshToken findRefreshTokenByRefreshToken(final String refreshToken){
		return refreshTokenRepository.findById(refreshToken)
				.orElseThrow(() -> new NotFoundException("토큰이 존재하지 않습니다."));
	}

	private Member findMemberByRefreshToken(Long memberId){
		return memberRepository.findById(memberId)
				.orElseThrow(() -> new NotFoundException("회원이 존재하지 않습니다."));
	}


	public String generateRefreshToken(Long memberId){
		String refreshToken = UUID.randomUUID().toString();
		RefreshToken redis = RefreshToken.builder()
				.refreshToken(refreshToken)
				.memberId(memberId)
				.build();
		refreshTokenRepository.save(redis);
		return refreshToken;
	}

	public String generateRefreshToken(String username){
		String refreshToken = UUID.randomUUID().toString();
		RefreshToken redis = RefreshToken.builder()
				.refreshToken(refreshToken)
				.username(username)
				.build();
		refreshTokenRepository.save(redis);
		return refreshToken;
	}


	public Optional<TempMemberDTO> getTempMemberByRefreshToken(final String refreshToken){
		RefreshToken checkedRefreshToken = findRefreshTokenByRefreshToken(refreshToken);
		return tempMemberRepository.findByUsername(checkedRefreshToken.getUsername());
	}


	public void deleteRefreshToken(String refreshToken){
		refreshTokenRepository.deleteById(refreshToken);
	}

	public AccessAndRefreshToken getAccessAndRefreshToken(final String refreshToken){
		Member member = getMemberByRefreshToken(refreshToken);
		String newAccessToken = jwtUtil.createAccessJwt(member.getUsername(), member.getRole().getKey(), member.getMemberId(), member.getNickname());
        String newRefreshToken = generateRefreshToken(member.getMemberId());

		deleteRefreshToken(refreshToken);

		return AccessAndRefreshToken.builder()
				.accessToken(newAccessToken)
				.refreshToken(newRefreshToken).build();
	}


}
