package com.ssafy.moa2zi.term.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "term_details")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TermDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long termDetailId;

    @Column(nullable = false, columnDefinition = "INT UNSIGNED")
    private Long termId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;


}
