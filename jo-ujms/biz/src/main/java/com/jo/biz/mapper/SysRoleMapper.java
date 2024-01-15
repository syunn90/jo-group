package com.jo.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jo.api.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author Jo
 * @since 2017-10-29
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

	/**
	 * 通过用户ID，查询角色信息
	 * @param userId
	 * @return
	 */
	SysRole listRolesByUserId(Long userId);

}
