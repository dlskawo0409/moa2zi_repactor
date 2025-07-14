package com.ssafy.moa2zi.category.domain;

import com.ssafy.moa2zi.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "categories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Category extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    private Long memberId;

    private Long parentId;

    @NotNull
    private Integer level;

    @NotNull
    private String categoryName;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CategoryType categoryType;

}