package io.renren.modules.student.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 学习资源表
 *
 * @author Haili 695536881@qq.com
 * @since 1.0.0 2025-11-02
 */
@Data
@TableName("learning_resource")
public class LearningResourceEntity {

    /**
     * 
     */
	private Long id;
    /**
     * 资源标题
     */
	private String title;
    /**
     * 资源链接
     */
	private String url;
    /**
     * 类型(视频/文章/习题)
     */
	private String resourceType;
    /**
     * 适用招考类别
     */
	private String examCategory;
    /**
     * 适用科目
     */
	private String subjectName;
    /**
     * 关联知识点
     */
	private String knowledgePointIds;
    /**
     * 难度等级
     */
	private Integer difficulty;
    /**
     * 来源平台
     */
	private String sourcePlatform;
    /**
     * 资源描述
     */
	private String description;
    /**
     * 
     */
	private Date createDate;
    /**
     * 
     */
	private Date updateDate;
}