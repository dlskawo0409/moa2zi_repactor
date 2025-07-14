package com.ssafy.moa2zi.auth.application;

import com.ssafy.moa2zi.auth.domain.CustomOAuth2User;
import com.ssafy.moa2zi.auth.domain.TempMemberDTO;
import com.ssafy.moa2zi.auth.domain.TempMemberRepository;
import com.ssafy.moa2zi.auth.dto.response.GitHubResponse;
import com.ssafy.moa2zi.auth.dto.response.KakaoResponse;
import com.ssafy.moa2zi.auth.dto.response.OAuth2Response;
import com.ssafy.moa2zi.member.domain.Member;
import com.ssafy.moa2zi.member.domain.MemberRepository;
import com.ssafy.moa2zi.member.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;
    private final TempMemberRepository tempMemberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        System.out.println(oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response;

        // Kakao 로그인 처리
        if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        }
        // GitHub 로그인 등 추가할 경우
         else if (registrationId.equals("github")) {
            oAuth2Response = new GitHubResponse(oAuth2User.getAttributes());
         }
        else {
            oAuth2Response = null;
            return null;
        }

        // 리소스 서버에서 발급받은 정보로 사용자를 특정할 아이디 생성
        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();

//        AtomicBoolean exist = new AtomicBoolean(true); // 기존 사용자 여부 플래그

        Member existData = memberRepository.findByUsername(username)
                .orElseGet(() -> {
//                    exist.set(false); // 새로 가입한 사용자일 경우 false로 설정
                    Member newMember = Member.builder()
                            .username(username)
                            .nickname(oAuth2Response.getName())
                            .profileImage(oAuth2Response.getProfile())
                            .role(Role.from("ROLE_USER"))
                            .build();
                    TempMemberDTO tempMemberDTO = new TempMemberDTO(newMember);
                    tempMemberRepository.save(tempMemberDTO);

//                    return memberRepository.save(newMember); // 새 사용자 저장

                    return newMember;
                });

        existData.setNickname(oAuth2Response.getName());
        existData.setProfileImage(oAuth2Response.getProfile());
//        if(exist.get()){
//            existData.setNickname(oAuth2Response.getName());
//            existData.setProfileImage(oAuth2Response.getProfile());
//        }
//        else{
//            tempMemberRepository.save()
//        }

        return new CustomOAuth2User(existData);

    }

}
