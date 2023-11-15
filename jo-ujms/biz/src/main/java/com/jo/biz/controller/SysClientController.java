package com.jo.biz.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jo.api.entity.SysOauthClientDetails;
import com.jo.biz.service.SysOauthClientDetailsService;
import com.jo.common.annotation.Inner;
import com.jo.common.util.R;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xtc
 * @date 2023/11/15
 */
@RestController
@AllArgsConstructor
@RequestMapping("/client")
@Tag(description = "client", name = "客户端管理模块")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class SysClientController {

    private final SysOauthClientDetailsService clientDetailsService;

    @Inner(false)
    @GetMapping("/getClientDetailsById/{clientId}")
    public R getClientDetailsById(@PathVariable String clientId) {
        return R.ok(clientDetailsService.getOne(
                Wrappers.<SysOauthClientDetails>lambdaQuery().eq(SysOauthClientDetails::getClientId, clientId), false));
    }

}
