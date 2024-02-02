package com.jo.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * <p>
 * 菜单权限表
 * </p>
 *
 * @author Jo
 */
@Data
@Schema(description = "菜单")
@EqualsAndHashCode(callSuper = true)
public class SysMenu extends Model<SysMenu> {

	private static final long serialVersionUID = 1L;

	/**
	 * 菜单ID
	 */
	@TableId(value = "menu_id", type = IdType.ASSIGN_ID)
	@Schema(description = "菜单id")
	private Long menuId;

	/**
	 * 菜单名称
	 */
	@NotBlank(message = "菜单名称不能为空")
	@Schema(description = "菜单名称")
	private String name;

	/**
	 * 菜单名称
	 */
	@Schema(description = "菜单名称")
	private String enName;

	/**
	 * 菜单权限标识
	 */
	@Schema(description = "菜单权限标识")
	private String permission;

	/**
	 * 父菜单ID
	 */
	@NotNull(message = "菜单父ID不能为空")
	@Schema(description = "菜单父id")
	private Long parentId;

	/**
	 * 图标
	 */
	@Schema(description = "菜单图标")
	private String icon;

	/**
	 * 前端路由标识路径，默认和 comment 保持一致 过期
	 */
	@Schema(description = "前端路由标识路径")
	private String path;

	/**
	 * 菜单显示隐藏控制
	 */
	@Schema(description = "菜单是否显示")
	private String visible;

	/**
	 * 排序值
	 */
	@Schema(description = "排序值")
	private Integer sortOrder;

	/**
	 * 菜单类型 （0菜单 1按钮）
	 */
	@NotNull(message = "菜单类型不能为空")
	@Schema(description = "菜单类型,0:菜单 1:按钮")
	private String menuType;

	/**
	 * 路由缓冲
	 */
	@Schema(description = "路由缓冲")
	private String keepAlive;

	@Schema(description = "菜单是否内嵌")
	private String embedded;

	/**
	 * 创建人
	 */
	@TableField(fill = FieldFill.INSERT)
	@Schema(description = "创建人")
	private String createBy;

	/**
	 * 修改人
	 */
	@TableField(fill = FieldFill.UPDATE)
	@Schema(description = "修改人")
	private String updateBy;

	/**
	 * 创建时间
	 */
	@TableField(fill = FieldFill.INSERT)
	@Schema(description = "创建时间")
	private LocalDateTime createTime;

	/**
	 * 更新时间
	 */
	@TableField(fill = FieldFill.UPDATE)
	@Schema(description = "更新时间")
	private LocalDateTime updateTime;

	/**
	 * 0--正常 1--删除
	 */
	@TableLogic
	@TableField(fill = FieldFill.INSERT)
	@Schema(description = "删除标记,1:已删除,0:正常")
	private String delFlag;

	@Schema(description = "模块名")
	private String component;
}
