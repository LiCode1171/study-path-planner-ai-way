package io.renren.modules.student.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 学习资源表
 *
 * @author Haili 695536881@qq.com
 * @since 1.0.0 2025-11-02
 */
@Data
@Schema(name = "学习资源表")
public class LearningResourceDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@SchemaProperty(name = "")
	private Long id;

	@SchemaProperty(name = "资源标题")
	private String title;

	@SchemaProperty(name = "资源链接")
	private String url;

	@SchemaProperty(name = "类型(视频/文章/习题)")
	private String resourceType;

	@SchemaProperty(name = "适用招考类别")
	private String examCategory;

	@SchemaProperty(name = "适用科目")
	private String subjectName;

	@SchemaProperty(name = "关联知识点")
	private String knowledgePointIds;

	@SchemaProperty(name = "难度等级")
	private Integer difficulty;

	@SchemaProperty(name = "来源平台")
	private String sourcePlatform;

	@SchemaProperty(name = "资源描述")
	private String description;

	@SchemaProperty(name = "")
	private Date createDate;

	@SchemaProperty(name = "")
	private Date updateDate;


}
