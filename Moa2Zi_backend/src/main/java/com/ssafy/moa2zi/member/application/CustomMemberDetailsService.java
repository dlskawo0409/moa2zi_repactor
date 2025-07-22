package com.ssafy.moa2zi.member.application;

import com.ssafy.moa2zi.member.domain.Member;
import com.ssafy.moa2zi.member.domain.MemberRepository;
import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

@Service
@RequiredArgsConstructor
public class CustomMemberDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username){
        Member userData = findMemberByUsername(username);

        return new CustomMemberDetails(userData);
    }

    private Member findMemberByUsername(String username){
        return memberRepository.findByUsername(username)
                .orElseThrow(()-> new NotFoundException("회원이 존재하지않습니다."));
    }

}
