package com.jo.biz.service.impl;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jo.api.dto.UserInfo;
import com.jo.api.entity.SysMenu;
import com.jo.api.entity.SysUser;
import com.jo.biz.mapper.SysUserMapper;
import com.jo.biz.service.SysMenuService;
import com.jo.biz.service.SysRoleService;
import com.jo.biz.service.SysUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xtc
 * @date 2023/11/3
 */
@Service
@AllArgsConstructor
public class SysUserServiceImpl  extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysRoleService sysRoleService;

    private final SysMenuService sysMenuService;
    @Override
    public UserInfo findUserInfo(SysUser sysUser) {
        UserInfo userInfo = new UserInfo();
        userInfo.setSysUser(sysUser);

        // 设置角色列表 （ID）
        Long roleId = sysRoleService.findRolesByUserId(sysUser.getUserId()).getRoleId();

        UserInfo.Roles r = new UserInfo.Roles();
        r.setRole(roleId);

        List<String> permissionList = sysMenuService.findMenuByRoleId(roleId)
                .stream()
                .filter(menu -> StrUtil.isNotEmpty(menu.getPermission()))
                .map(SysMenu::getPermission)
                .collect(Collectors.toList());
        r.setPermissions(ArrayUtil.toArray(permissionList, String.class));

        userInfo.setRoles(r);


//        userInfo.setRoles(ArrayUtil.toArray(roleIds, Long.class));
//
//        // 设置权限列表（menu.permission）
//        Set<String> permissions = new HashSet<>();
//        roleIds.forEach(roleId -> {
//            List<String> permissionList = sysMenuService.findMenuByRoleId(roleId)
//                    .stream()
//                    .filter(menu -> StrUtil.isNotEmpty(menu.getPermission()))
//                    .map(SysMenu::getPermission)
//                    .collect(Collectors.toList());
//            permissions.addAll(permissionList);
//        });
//        userInfo.setPermissions(ArrayUtil.toArray(permissions, String.class));
        return userInfo;
    }

}
