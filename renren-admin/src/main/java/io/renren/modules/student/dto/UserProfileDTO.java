package io.renren.modules.student.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 用户专升本信息扩展表
 *
 * @author Haili 695536881@qq.com
 * @since 1.0.0 2025-11-02
 */
@Data
@Schema(name = "用户专升本信息扩展表")
public class UserProfileDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@SchemaProperty(name = "")
	private Long id;

	@SchemaProperty(name = "关联用户ID")
	private Long userId;

	@SchemaProperty(name = "招考类别(理工类/经管类/医学类等)")
	private String examCategory;

	@SchemaProperty(name = "目标院校")
	private String targetSchool;

	@SchemaProperty(name = "当前专业")
	private String currentMajor;

	@SchemaProperty(name = "目标专业")
	private String targetMajor;

	@SchemaProperty(name = "备考月数")
	private Integer preparationMonths;

	@SchemaProperty(name = "每日学习时间(小时)")
	private Integer dailyStudyTime;

	@SchemaProperty(name = "公共基础课类型(大学语文/高等数学)")
	private String publicCourseType;

	@SchemaProperty(name = "")
	private Date createDate;

	@SchemaProperty(name = "")
	private Date updateDate;


}
