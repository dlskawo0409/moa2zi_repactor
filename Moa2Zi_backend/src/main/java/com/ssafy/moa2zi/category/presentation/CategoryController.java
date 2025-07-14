package com.ssafy.moa2zi.category.presentation;

import com.ssafy.moa2zi.category.application.CategoryService;
import com.ssafy.moa2zi.category.domain.CategoryType;
import com.ssafy.moa2zi.category.dto.response.CategoryTypeListSearchResponse;
import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@Tag(name = "Category", description = "카테고리 API")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "카테고리 종류 가져오기", description = "상위 카테고리와 그에 해당하는 서브 카테고리를 가져옵니다.")
    public ResponseEntity<List<CategoryTypeListSearchResponse>> getCategoryTypeList(
            @RequestParam(name="categoryId", required = false) Long categoryId,
            @RequestParam(name="level", required = false) Integer level,
            @RequestParam(name="categoryType") @NotNull CategoryType categoryType,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {
        return ResponseEntity.ok(categoryService.getCategoryTypeList(categoryId, categoryType, level, loginMember));
    }

}
