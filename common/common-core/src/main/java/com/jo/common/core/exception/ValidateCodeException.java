package com.jo.common.core.exception;

/**
 * @author xtc
 * @date 2023/12/15
 */
public class ValidateCodeException extends RuntimeException {

	private static final long serialVersionUID = -7285211528095468156L;

	public ValidateCodeException() {

	}

	public ValidateCodeException(String msg) {
		super(msg);
	}

}
