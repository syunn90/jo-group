package com.jo.rpc.chain.dealing;

import com.jo.rpc.chain.Dealing;
import com.jo.rpc.chain.DealingContext;
import com.jo.rpc.extension.DuplicatedMarker;
import com.jo.rpc.protocol.Command;
import com.jo.rpc.protocol.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jo
 * @date 2024/7/9
 */
public class DuplicateDealing implements Dealing {
    private static final Logger logger = LoggerFactory.getLogger(DuplicateDealing.class);

    private DuplicatedMarker duplicatedMarker;

    public DuplicateDealing(DuplicatedMarker duplicatedMarker) {
        this.duplicatedMarker = duplicatedMarker;
    }

    @Override
    public void deal(DealingContext context) {
        Command command = context.getCommand();
        // 只有收到请求才需要去重
        if (command.isRequest()) {
            Long seq = command.getSeq();
            if (duplicatedMarker.mark(seq)) {
                logger.warn("Received duplicate request seq：[{}], ignore it", seq);
                RpcResponse response = RpcResponse.duplicateRequest(seq);
                context.getConnection().send(response);
            } else {
                context.nextDealing();
            }
        } else {
            context.nextDealing();
        }
    }

}
