package com.ssafy.moa2zi.common.exception;

import org.springframework.http.HttpStatus;

import com.google.firebase.ErrorCode;

import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {

	private final ErrorCode<?> errorCode;
	private final HttpStatus status;

	public GlobalException(ErrorCode<?> errorCode, HttpStatus status) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
		this.status = status;
	}

}