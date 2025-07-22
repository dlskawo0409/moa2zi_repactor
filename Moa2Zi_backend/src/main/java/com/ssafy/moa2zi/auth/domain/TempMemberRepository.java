package com.ssafy.moa2zi.auth.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface TempMemberRepository extends CrudRepository<TempMemberDTO, Long> {
    Optional<TempMemberDTO> findByUsername(String username);
}
