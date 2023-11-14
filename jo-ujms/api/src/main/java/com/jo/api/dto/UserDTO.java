package com.jo.api.dto;

import com.jo.api.entity.SysUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author Jo
 */
@Data
@Schema(description = "系统用户传输对象")
@EqualsAndHashCode(callSuper = true)
public class UserDTO extends SysUser {

	/**
	 * 角色ID
	 */
	@Schema(description = "角色id集合")
	private List<Long> role;

	/**
	 * 部门id
	 */
	@Schema(description = "部门id")
	private Long deptId;

	/**
	 * 岗位ID
	 */
	private List<Long> post;

	/**
	 * 新密码
	 */
	@Schema(description = "新密码")
	private String newpassword1;

}
