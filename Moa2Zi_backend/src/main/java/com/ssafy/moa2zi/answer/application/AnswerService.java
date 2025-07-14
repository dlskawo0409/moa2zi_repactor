package com.ssafy.moa2zi.answer.application;

import org.apache.coyote.BadRequestException;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import com.ssafy.moa2zi.answer.domain.Answer;
import com.ssafy.moa2zi.answer.domain.AnswerRepository;
import com.ssafy.moa2zi.answer.dto.request.AnswerCreateRequest;
import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnswerService {

	private final AnswerRepository answerRepository;

	public void createAnswer(
		AnswerCreateRequest answerCreateRequest,
		CustomMemberDetails loginMember
	) throws BadRequestException {

		if(answerRepository.existsByQuizIdAndMemberId(answerCreateRequest.quizId(), loginMember.getMemberId())){
			throw new BadRequestException("이미 정답이 존재합니다.");
		}

		answerRepository.save(Answer.builder()
				.quizId(answerCreateRequest.quizId())
				.memberId(loginMember.getMemberId())
				.submission(answerCreateRequest.submission())
				.build());
	}

}
