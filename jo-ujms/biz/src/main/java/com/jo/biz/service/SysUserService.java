package com.jo.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jo.api.dto.UserInfo;
import com.jo.api.entity.SysUser;

/**
 * @author xtc
 * @date 2023/11/3
 */
public interface SysUserService extends IService<SysUser> {
    /**
     *
     * @param user
     * @return
     */
    UserInfo findUserInfo(SysUser user);
}
