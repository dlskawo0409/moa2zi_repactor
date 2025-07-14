package com.ssafy.moa2zi.geo.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "dongs")
public class Dong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dong_id")
    private Long id;

    @Column(name = "dong_code")
    private int dongCode;

    @Column(name = "gugun_code")
    private int gugunCode;

    @Column(length = 20, name = "dong_name")
    private String dongName;


}
