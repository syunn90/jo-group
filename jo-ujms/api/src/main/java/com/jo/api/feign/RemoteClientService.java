package com.jo.api.feign;

import com.jo.common.constant.SecurityConstants;
import com.jo.api.entity.SysOauthClientDetails;
import com.jo.common.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @author xtc
 * @date 2023/11/15
 */
@FeignClient(contextId = "clientService", value = "biz")
public interface RemoteClientService {

    /**
     * 通过clientId 查询客户端信息
     * @param clientId 用户名
     * @param from 调用标志
     * @return R
     */
    @GetMapping("/client/getClientDetailsById/{clientId}")
    R<SysOauthClientDetails> getClientDetailsById(@PathVariable("clientId") String clientId,
                                                  @RequestHeader(SecurityConstants.FROM) String from);

}
