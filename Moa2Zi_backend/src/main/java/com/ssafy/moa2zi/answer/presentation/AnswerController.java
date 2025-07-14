package com.ssafy.moa2zi.answer.presentation;

import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.moa2zi.answer.application.AnswerService;
import com.ssafy.moa2zi.answer.dto.request.AnswerCreateRequest;
import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/answers")
@RequiredArgsConstructor
public class AnswerController {

	private  final AnswerService answerService;

	@PostMapping
	public ResponseEntity<Void> createAnswer(
		@Valid @RequestBody  AnswerCreateRequest answerCreateRequest,
		@AuthenticationPrincipal CustomMemberDetails loginMember
	) throws BadRequestException {

		answerService.createAnswer(answerCreateRequest, loginMember);

		return ResponseEntity.created(null).build();
	};

}
