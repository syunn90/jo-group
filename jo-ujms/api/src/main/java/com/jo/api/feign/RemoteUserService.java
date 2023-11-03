package com.jo.api.feign;

import com.jo.api.dto.UserDTO;
import com.jo.api.dto.UserInfo;
import com.jo.common.constant.SecurityConstants;
import com.jo.common.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @author xtc
 * @date 2023/11/3
 */
@FeignClient(contextId = "testService", value= "biz")
public interface RemoteUserService {

    /**
     * 通过用户名查询用户、角色信息
     * @param user 用户查询对象
     * @param from 调用标志
     * @return R
     */
    @GetMapping("/user/info/query")
    R<UserInfo> info(@SpringQueryMap UserDTO user, @RequestHeader(SecurityConstants.FROM) String from);

}
