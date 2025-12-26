package io.renren.modules.student.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 学习计划项表
 *
 * @author Haili 695536881@qq.com
 * @since 1.0.0 2025-11-28
 */
@Data
@Schema(name = "学习计划项表")
public class StudyPlanItemDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SchemaProperty(name = "")
    private Long id;

    @SchemaProperty(name = "学习计划ID")
    private Long planId;

    @SchemaProperty(name = "知识点ID")
    private Long knowledgePointId;

    @SchemaProperty(name = "第几天")
    private Integer dayIndex;

    @SchemaProperty(name = "排序")
    private Integer sort;

    @SchemaProperty(name = "完成状态：0-未完成 1-已完成 2-补交")
    private Integer completed;

    @SchemaProperty(name = "完成时间")
    private Date completionDate;

    @SchemaProperty(name = "")
    private Date createDate;

    @SchemaProperty(name = "")
    private Date updateDate;

    @SchemaProperty(name = "科目名称")
    private String subjectName;

    @SchemaProperty(name = "是否复习项 0-否 1-是")
    private Integer isReview;

    @SchemaProperty(name = "拆分部分（第几部分），用于跨天学习的知识点")
    private Integer splitPart;

    @SchemaProperty(name = "总部分数，用于跨天学习的知识点")
    private Integer totalParts;

    @SchemaProperty(name = "本次分配的时长（小时），用于跨天学习的知识点")
    private Integer splitHours;


}
