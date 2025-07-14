package com.ssafy.moa2zi.common.exception;

public class ConflictException extends GlobalException {

	public ConflictException(ErrorCode<?> errorCode) {
		super(errorCode, HttpStatus.CONFLICT);
	}

	public ConflictException(String message) { super(new ErrorCode<>(message), HttpStatus.CONFLICT); }

}