package io.renren.modules.student.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 知识点库
 *
 * @author Haili 695536881@qq.com
 * @since 1.0.0 2025-11-02
 */
@Data
@TableName("knowledge_point")
public class KnowledgePointEntity {

    /**
     * 
     */
	private Long id;
    /**
     * 招考类别
     */
	private String examCategory;
    /**
     * 科目名称
     */
	private String subjectName;
    /**
     * 章节
     */
	private String chapter;
    /**
     * 知识点名称
     */
	private String pointName;
    /**
     * 难度等级1-5
     */
	private Integer difficulty;
    /**
     * 预计学习时长(小时)
     */
	private Integer estimatedHours;
    /**
     * 考试权重1-10
     */
	private Integer weight;
    /**
     * 前置知识点ID
     */
	private String prerequisiteIds;
    /**
     * 知识点描述
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