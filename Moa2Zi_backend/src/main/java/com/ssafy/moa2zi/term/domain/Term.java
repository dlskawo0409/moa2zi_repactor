package com.ssafy.moa2zi.term.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "terms")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Term {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long termId;

    @Column(nullable = false)
    private String title;

    @Column
    private String subTitle;

    @Column(nullable = false)
    private TermType termType;

}
