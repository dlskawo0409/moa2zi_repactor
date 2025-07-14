package com.ssafy.moa2zi.geo.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DongRepository extends JpaRepository<Dong, Long> {

    List<Dong> findByGugunCode(int gugunCode);
}
