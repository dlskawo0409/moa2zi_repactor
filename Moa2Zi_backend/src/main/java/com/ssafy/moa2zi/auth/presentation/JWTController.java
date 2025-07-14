package com.ssafy.moa2zi.auth.presentation;

import com.ssafy.moa2zi.auth.application.RedisRefreshTokenService;
//import com.ssafy.moa2zi.auth.domain.RefreshEntity;
//import com.ssafy.moa2zi.auth.domain.RefreshRepository;
import com.ssafy.moa2zi.auth.domain.TempMemberDTO;
import com.ssafy.moa2zi.auth.dto.response.AccessAndRefreshToken;
import com.ssafy.moa2zi.auth.jwt.JWTUtil;

import com.ssafy.moa2zi.member.domain.MemberRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.webjars.NotFoundException;

import java.time.Duration;

@Slf4j
@RestController
@RequiredArgsConstructor
public class JWTController {
	private final JWTUtil jwtUtil;
//	private RefreshRepository refreshRepository;
	private final RedisRefreshTokenService redisRefreshTokenService;
	@Value("${spring.jwt.access-token-name}") String accessTokenName;
	@Value("${spring.jwt.refresh-token-name}") String refreshTokenName;
	@Value("${spring.jwt.oauth-token-name}") String oauthTokenName;
	@PostMapping("/reissue")
	public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response){

		//get refresh token
		String refresh = null;
		Cookie[] cookies = request.getCookies();

		if(cookies == null || cookies.length == 0){
			return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
		}

		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(refreshTokenName)) {
				refresh = cookie.getValue();
			}
		}


		if (refresh == null) {

			//response status code
			return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
		}

		//expired check
//		try {
//			jwtUtil.isExpired(refresh);
//		} catch (ExpiredJwtException e) {
//
//			//response status code
//			return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
//		}

		// 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
		// String category = jwtUtil.getCategory(refresh);
		//
		// if (!category.equals("refresh")) {
		//
		// 	//response status code
		// 	return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
		// }
		//
		// //DB에 저장되어 있는지 확인
		// Boolean isExist = refreshRepository.existsByRefresh(refresh);
		// if (!isExist) {
		//
		// 	//response body
		// 	return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
		// }

//		String username = jwtUtil.getUsername(refresh);
//		String role = jwtUtil.getRole(refresh).getKey();
//		Long memberId = jwtUtil.getMemberId(refresh);

		//make new JWT
//		String newAccess = jwtUtil.createJwt("access", username, role, memberId);
		// String newRefresh = jwtUtil.createJwt("refresh", username, role, 86400000L, memberId);
		//Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
		// refreshRepository.deleteByRefresh(refresh);
		// addRefreshEntity(username, newRefresh, 86400000L);

		//redis
		AccessAndRefreshToken accessAndRefreshToken = redisRefreshTokenService.getAccessAndRefreshToken(refresh);

		//response
		response.setHeader(accessTokenName, accessAndRefreshToken.accessToken());
//		response.addCookie(createCookie(refreshTokenName, accessAndRefreshToken.refreshToken()));
		ResponseCookie cookie = ResponseCookie.from(refreshTokenName, accessAndRefreshToken.refreshToken())
				.httpOnly(true)
				.secure(true)
				.path("/")
				.sameSite("None") // 중요!
				.maxAge(Duration.ofHours(4))
				.build();

		response.addHeader("Set-Cookie", cookie.toString());


		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/oauth2")
	public ResponseEntity<?> getJWTByCookie(HttpServletRequest request, HttpServletResponse response) {
		String authorization = null;
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(oauthTokenName)) {
				authorization = cookie.getValue();
			}
		}

		if (authorization == null) {
			//response status code
			return new ResponseEntity<>("authorization token null", HttpStatus.BAD_REQUEST);
		}

		//expired check
		try {
			jwtUtil.isExpired(authorization);
		} catch (ExpiredJwtException e) {

			//response status code
			return new ResponseEntity<>("authorization token expired", HttpStatus.BAD_REQUEST);
		}
		// 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
		String category = jwtUtil.getCategory(authorization);

		if (!category.equals(oauthTokenName)) {
			//response status code
			return new ResponseEntity<>("invalid authorization token", HttpStatus.BAD_REQUEST);
		}


		String username = jwtUtil.getUsername(authorization);
		String role = jwtUtil.getRole(authorization).getKey();
		Long memberId = jwtUtil.getMemberId(authorization);
		String nickname = jwtUtil.getNickname(authorization);

		//make new JWT
		String newAccess = jwtUtil.createAccessJwt( username, role, memberId, nickname);

		//response
		response.setHeader(accessTokenName, newAccess);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/members/temp")
	public ResponseEntity<?> getMemberTemp(HttpServletRequest request, HttpServletResponse response){
		String authorization = null;
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(refreshTokenName)) {
				authorization = cookie.getValue();
			}
		}

		if (authorization == null) {
			//response status code
			return new ResponseEntity<>("authorization token null", HttpStatus.BAD_REQUEST);
		}

		TempMemberDTO tempMemberDTO = redisRefreshTokenService.getTempMemberByRefreshToken(authorization)
				.orElseThrow(() -> new NotFoundException("토큰에 해당하는 값이 존재하지 않습니다."));

		return ResponseEntity.ok(tempMemberDTO);

	}



	private Cookie createCookie(String key, String value) {

		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(14440);
		cookie.setSecure(false);
//		cookie.setPath("/api/v1");
		cookie.setPath("/");
//		cookie.setHttpOnly(true);

		return cookie;
	}

//	private void addRefreshEntity(String username, String refresh, Long expiredMs) {
//
//		Date date = new Date(System.currentTimeMillis() + expiredMs);
//
//		RefreshEntity refreshEntity = RefreshEntity.builder()
//			.username(username)
//			.refresh(refresh)
//			.expiration(date.toString())
//			.build();
//
//		refreshRepository.save(refreshEntity);
//	}

}
