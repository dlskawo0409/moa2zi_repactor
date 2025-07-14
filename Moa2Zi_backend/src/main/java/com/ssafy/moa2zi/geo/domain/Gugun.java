package com.ssafy.moa2zi.geo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "guguns")
public class Gugun {

    @Id
    @Column(name="gugun_code")
    private int gugunCode;

    @Column(name = "sido_code")
    private int sidoCode;

    @Column(name = "gugun_name", length = 20)
    private String gugunName;

}

