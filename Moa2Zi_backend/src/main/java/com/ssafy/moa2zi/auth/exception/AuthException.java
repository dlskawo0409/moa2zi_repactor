package com.ssafy.moa2zi.auth.exception;

import org.apache.coyote.BadRequestException;

public class AuthException {

	public static class AuthBadRequestException extends BadRequestException {
		public AuthBadRequestException(AuthErrorCode errorCode) {
			super(String.valueOf(new ErrorCode<>(errorCode.getCode(), errorCode.getMessage())));
		}
	}

}