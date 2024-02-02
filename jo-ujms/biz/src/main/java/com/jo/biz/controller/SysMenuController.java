package com.jo.biz.controller;

import com.jo.api.entity.SysMenu;
import com.jo.biz.service.SysMenuService;
import com.jo.common.core.util.R;
import com.jo.common.security.util.SecurityUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;

/**
 * @author xtc
 * @date 2024/1/30
 */
@RestController
@AllArgsConstructor
@RequestMapping("/menu")
@Tag(description = "menu", name = "菜单管理模块")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class SysMenuController {

    private final SysMenuService sysMenuService;
    /**
     * 返回当前用户的树形菜单集合
     * @param type 类型
     * @param parentId 父节点ID
     * @return 当前用户的树形菜单
     */
    @GetMapping("/tree")
    public R getMenuTree(String type, Long parentId) {
        // 获取符合条件的菜单
        var all = new HashSet<SysMenu>();
        SecurityUtils.getRoles().forEach(roleId -> all.addAll(sysMenuService.findMenuByRoleId(roleId)));
        return R.ok(sysMenuService.treeMenu(all, type, parentId));
    }

    /**
     * 返回当前用户的树形菜单集合
     * @param type 类型
     * @param parentId 父节点ID
     * @return 当前用户的树形菜单
     */
    @GetMapping("/list")
    public R getMenuList(String type, Long parentId) {
        // 获取符合条件的菜单
        var all = new HashSet<SysMenu>();
        SecurityUtils.getRoles().forEach(roleId -> all.addAll(sysMenuService.findMenuByRoleId(roleId)));
        return R.ok(sysMenuService.listMenu(all, type, parentId));
    }

}
