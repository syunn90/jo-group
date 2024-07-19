package com.jo.rpc.comm.exception;

/**
 * @author Jo
 * @date 2024/7/8
 */
public class SeqGeneratorException extends RuntimeException {

    public SeqGeneratorException() {
    }

    public SeqGeneratorException(String message) {
        super(message);
    }

    public SeqGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }

}
