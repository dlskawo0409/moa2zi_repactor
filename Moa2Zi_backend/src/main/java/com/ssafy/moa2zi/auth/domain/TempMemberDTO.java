package com.ssafy.moa2zi.auth.domain;

import com.ssafy.moa2zi.member.domain.Member;
import com.ssafy.moa2zi.member.domain.Role;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "tempMember", timeToLive = 14440)
public class TempMemberDTO {

    @Id
    private String username;
    private String nickname;
    private String profileImage;
    private Role role;

    public TempMemberDTO(Member member){
        this.username = member.getUsername();
        this.nickname = member.getNickname();
        this.profileImage = member.getProfileImage();
        this.role = member.getRole();
    }

}
