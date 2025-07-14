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
@Table(name = "sidos")
public class Sido {

    @Id
    @Column(name = "sido_code")
    private int sidoCode;

    @Column(length = 20, name = "sido_name")
    private String sidoName;

}
