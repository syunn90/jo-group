package com.jo.biz.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jo.api.entity.SysUser;
import com.jo.biz.service.SysUserService;
import com.jo.common.core.exception.ErrorCodes;
import com.jo.common.core.util.MsgUtils;
import com.jo.common.core.util.R;
import com.jo.common.security.annotation.Inner;
import com.jo.common.security.util.SecurityUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xtc
 * @date 2023/11/3
 */

@RestController
@AllArgsConstructor
@RequestMapping("/user")
@Tag(description = "user", name = "用户管理模块")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class SysUserController {

    private final SysUserService userService;

    /**
     * 获取指定用户全部信息
     * @return 用户信息
     */
    @Inner
    @GetMapping(value = { "/info/query" })
    public R info(@RequestParam(required = true) String username,
                  @RequestParam(required = false) String phone) {
        SysUser user = userService.getOne(Wrappers.<SysUser>query()
                .lambda()
                .eq(StrUtil.isNotBlank(username), SysUser::getUsername, username)
                .eq(StrUtil.isNotBlank(phone), SysUser::getPhone, phone));
        if (user == null) {
            return R.failed(MsgUtils.getMessage(ErrorCodes.SYS_USER_USERINFO_EMPTY, username));
//            return R.failed("Failed", username);
        }
        return R.ok(userService.findUserInfo(user));
    }

    /**
     * 获取当前用户全部信息
     * @return 用户信息
     */
    @GetMapping(value = { "/info" })
    public R info() {
        String username = SecurityUtils.getUser().getUsername();
        SysUser user = userService.getOne(Wrappers.<SysUser>query().lambda().eq(SysUser::getUsername, username));
        if (user == null) {
            return R.failed("获取用户信息失败");
        }
        return R.ok(userService.findUserInfo(user));
    }
}
