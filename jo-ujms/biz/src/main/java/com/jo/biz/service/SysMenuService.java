package com.jo.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jo.api.entity.SysMenu;

import java.util.List;

/**
 * <p>
 * 菜单权限表 服务类
 * </p>
 *
 * @author Jo
 * @since 2017-10-29
 */
public interface SysMenuService extends IService<SysMenu> {

	/**
	 * 通过角色编号查询URL 权限
	 * @param roleId 角色ID
	 * @return 菜单列表
	 */
	List<SysMenu> findMenuByRoleId(Long roleId);
//
//	/**
//	 * 级联删除菜单
//	 * @param id 菜单ID
//	 * @return 成功、失败
//	 */
//	R removeMenuById(Long id);
//
//	/**
//	 * 更新菜单信息
//	 * @param sysMenu 菜单信息
//	 * @return 成功、失败
//	 */
//	Boolean updateMenuById(SysMenu sysMenu);
//
//	/**
//	 * 构建树
//	 * @param parentId 父节点ID
//	 * @param menuName 菜单名称
//	 * @return
//	 */
//	List<Tree<Long>> treeMenu(Long parentId, String menuName, String type);
//
//	/**
//	 * 查询菜单
//	 * @param voSet
//	 * @param parentId
//	 * @return
//	 */
//	List<Tree<Long>> filterMenu(Set<SysMenu> voSet, String type, Long parentId);

}
