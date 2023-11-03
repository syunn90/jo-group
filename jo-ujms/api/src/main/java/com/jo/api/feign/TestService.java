package com.jo.api.feign;

import com.jo.common.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import sun.security.util.SecurityConstants;

/**
 * @author xtc
 * @date 2023/11/2
 */
@FeignClient(contextId = "testService", value= "biz")
public interface TestService {

    @GetMapping("/test/controller")
    R info();
}
