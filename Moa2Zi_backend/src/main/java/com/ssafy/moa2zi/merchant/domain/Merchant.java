package com.ssafy.moa2zi.merchant.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "merchants")
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "merchant_id")
    private Long id;

    private String merchantName;

    private String branchName;

    private String categoryTop;

    private String categoryMedium;

    private String categoryLow;

    private Integer sidoCode;

    private Integer gugunCode;

    private Integer dongCode;

    private String jibunAddress;

    private String roadAddress;

    private Double longitude;

    private Double latitude;

    @Column(columnDefinition = "POINT SRID 4326")
    private Point coordinate;

    @Column(name = "geohash_code", length = 12)
    private String geohashCode;

//    @NotNull
//    private Byte createdBy;

}
