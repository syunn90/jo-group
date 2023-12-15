package com.jo.common.core.exception;

/**
 * @author xtc
 * @date 2023/12/15
 */
public class HttpCollectException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public HttpCollectException(String message) {
        super(message);
    }

    public HttpCollectException(Throwable cause) {
        super(cause);
    }

}
