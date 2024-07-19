package com.jo.rpc.comm.exception;

public class NodeException extends RuntimeException{

    public NodeException() {
    }

    public NodeException(String message) {
        super(message);
    }

    public NodeException(Throwable cause) {
        super(cause);
    }

    public NodeException(String message, Throwable cause) {
        super(message, cause);
    }

}
