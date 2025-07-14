package com.ssafy.moa2zi.challenge.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record AwardCreateImageRequest(
        Long challengeTimeId,
        MultipartFile image
) {
}
