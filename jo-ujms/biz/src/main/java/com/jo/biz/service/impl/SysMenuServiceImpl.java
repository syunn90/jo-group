package com.jo.biz.service.impl;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jo.api.entity.SysMenu;
import com.jo.biz.mapper.SysMenuMapper;
import com.jo.biz.service.SysMenuService;
import com.jo.common.core.constant.CommonConstants;
import com.jo.common.core.constant.enums.MenuTypeEnum;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author xtc
 * @date 2023/11/3
 */
@Service
public class SysMenuServiceImpl  extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
    @Override
    public List<SysMenu> findMenuByRoleId(Long roleId) {
        return baseMapper.listMenusByRoleId(roleId);
    }

    /**
     * 查询菜单
     * @param all 全部菜单
     * @param type 类型
     * @param parentId 父节点ID
     * @return
     */
    @Override
    public List<Tree<Long>> treeMenu(Set<SysMenu> all, String type, Long parentId) {
        var collect = all.stream()
                .filter(menuTypePredicate(type))
                .map(getNodeFunction())
                .collect(Collectors.toList());

        Long parent = parentId == null ? CommonConstants.MENU_TREE_ROOT_ID : parentId;
        return TreeUtil.build(collect, parent);
    }

    /**
     * 查询菜单
     * @param all 全部菜单
     * @param type 类型
     * @param parentId 父节点ID
     * @return
     */
    @Override
    public List<TreeNode<Long>> listMenu(Set<SysMenu> all, String type, Long parentId) {
        return all.stream()
                .filter(menuTypePredicate(type))
                .map(getNodeFunction())
                .collect(Collectors.toList());
    }
    @NotNull
    private Function<SysMenu, TreeNode<Long>> getNodeFunction() {
        return menu -> {
            TreeNode<Long> node = new TreeNode<>();
            node.setId(menu.getMenuId());
            node.setName(menu.getName());
            node.setParentId(menu.getParentId());
            node.setWeight(menu.getSortOrder());
            // 扩展属性
            var extra = new HashMap<String,Object>();
            extra.put("path", menu.getPath());
            extra.put("menuType", menu.getMenuType());
            extra.put("permission", menu.getPermission());
            extra.put("sortOrder", menu.getSortOrder());
            extra.put("component", menu.getComponent());
            // 适配 vue3
            var meta = new HashMap<String,Object>();
            meta.put("title", menu.getName());
            meta.put("isLink", menu.getPath() != null && menu.getPath().startsWith("http") ? menu.getPath() : "");
            meta.put("isHide", !BooleanUtil.toBooleanObject(menu.getVisible()));
            meta.put("isKeepAlive", BooleanUtil.toBooleanObject(menu.getKeepAlive()));
            meta.put("isAffix", false);
            meta.put("isIframe", BooleanUtil.toBooleanObject(menu.getEmbedded()));
            meta.put("icon", menu.getIcon());
            // 增加英文
            meta.put("enName", menu.getEnName());

            extra.put("meta", meta);
            node.setExtra(extra);
            return node;
        };
    }

    /**
     * menu 类型断言
     * @param type 类型
     * @return Predicate
     */
    private Predicate<SysMenu> menuTypePredicate(String type) {
        return vo -> {
            if (MenuTypeEnum.TOP_MENU.getDescription().equals(type)) {
                return MenuTypeEnum.TOP_MENU.getType().equals(vo.getMenuType());
            }
            // 其他查询 左侧 + 顶部
            return !MenuTypeEnum.BUTTON.getType().equals(vo.getMenuType());
        };
    }
}
