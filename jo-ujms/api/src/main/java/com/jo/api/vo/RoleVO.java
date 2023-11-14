package com.jo.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Jo
 */
@Data
@Schema(description = "前端角色展示对象")
public class RoleVO {

	/**
	 * 角色id
	 */
	private Long roleId;

	/**
	 * 菜单列表
	 */
	private String menuIds;

}
