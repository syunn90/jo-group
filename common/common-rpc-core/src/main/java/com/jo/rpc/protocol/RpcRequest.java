package com.jo.rpc.protocol;

import java.util.Arrays;

/**
 * @author Jo
 * @date 2024/7/8
 */
public class RpcRequest extends Command {

    private static final long serialVersionUID = 1721928504017481170L;
    /**
     * 请求参数
     */
    private Object[] args;

    public RpcRequest() {
        super.setRequest(true);
    }

    public Object[] getArgs() {
        return args;
    }

    public RpcRequest setArgs(Object[] args) {
        this.args = args;
        return this;
    }

    @Override
    public String toString() {
        return "RpcRequest{" +
                "args=" + Arrays.toString(args) +
                "} " + super.toString();
    }
}
