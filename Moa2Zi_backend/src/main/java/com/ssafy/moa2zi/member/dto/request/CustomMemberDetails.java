package com.ssafy.moa2zi.member.dto.request;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ssafy.moa2zi.member.domain.Member;

public class CustomMemberDetails  implements UserDetails {

	private final Member member;

	public CustomMemberDetails(Member member) {

		this.member = member;
	}


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		Collection<GrantedAuthority> collection = new ArrayList<>();

		collection.add(new GrantedAuthority() {

			@Override
			public String getAuthority() {
				return member.getRole().getKey();
			}
		});


		return collection;
	}

	@Override
	public String getPassword() {

		return member.getPassword();
	}

	public Long getMemberId(){
		return member.getMemberId();
	}

	@Override
	public String getUsername() {

		return member.getUsername();
	}

	public String getNickname(){
		return member.getNickname();
	}

	@Override
	public boolean isAccountNonExpired() {

		return true;
	}

	@Override
	public boolean isAccountNonLocked() {

		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {

		return true;
	}

	@Override
	public boolean isEnabled() {

		return true;
	}

	public Member getMember() {
		return member;
	}

}