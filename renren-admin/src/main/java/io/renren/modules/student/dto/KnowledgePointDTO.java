package io.renren.modules.student.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 知识点库
 *
 * @author Haili 695536881@qq.com
 * @since 1.0.0 2025-11-02
 */
@Data
@Schema(name = "知识点库")
public class KnowledgePointDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@SchemaProperty(name = "")
	private Long id;

	@SchemaProperty(name = "招考类别")
	private String examCategory;

	@SchemaProperty(name = "科目名称")
	private String subjectName;

	@SchemaProperty(name = "章节")
	private String chapter;

	@SchemaProperty(name = "知识点名称")
	private String pointName;

	@SchemaProperty(name = "难度等级1-5")
	private Integer difficulty;

	@SchemaProperty(name = "预计学习时长(小时)")
	private Integer estimatedHours;

	@SchemaProperty(name = "考试权重1-10")
	private Integer weight;

	@SchemaProperty(name = "前置知识点ID")
	private String prerequisiteIds;

	@SchemaProperty(name = "知识点描述")
	private String description;

	@SchemaProperty(name = "")
	private Date createDate;

	@SchemaProperty(name = "")
	private Date updateDate;


}
