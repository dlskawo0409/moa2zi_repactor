package com.ssafy.moa2zi.geo.domain;

import org.apache.catalina.LifecycleState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GugunRepository extends JpaRepository<Gugun, Integer> {

    List<Gugun> findBySidoCode(int sidoCode);
}
