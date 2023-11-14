package com.jo.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jo.api.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 菜单权限表 Mapper 接口
 * </p>
 *
 * @author Jo
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

	/**
	 * 通过角色编号查询菜单
	 * @param roleId 角色ID
	 * @return
	 */
	List<SysMenu> listMenusByRoleId(Long roleId);

}
