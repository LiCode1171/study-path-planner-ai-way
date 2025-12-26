package io.renren.modules.student.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 用户科目基础水平表
 *
 * @author Haili 695536881@qq.com
 * @since 1.0.0 2025-11-02
 */
@Data
@Schema(name = "用户科目基础水平表")
public class UserSubjectLevelDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@SchemaProperty(name = "")
	private Long id;

	@SchemaProperty(name = "用户ID")
	private Long userId;

	@SchemaProperty(name = "科目类型(public/professional)")
	private String subjectType;

	@SchemaProperty(name = "科目名称")
	private String subjectName;

	@SchemaProperty(name = "基础水平1-5")
	private Integer level;

	@SchemaProperty(name = "")
	private Date createDate;

	@SchemaProperty(name = "")
	private Date updateDate;


}
