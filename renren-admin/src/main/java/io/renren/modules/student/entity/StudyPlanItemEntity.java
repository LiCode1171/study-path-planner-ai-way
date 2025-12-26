package io.renren.modules.student.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 学习计划项表
 *
 * @author Haili 695536881@qq.com
 * @since 1.0.0 2025-11-28
 */
@Data
@TableName("study_plan_item")
public class StudyPlanItemEntity {

    /**
     *
     */
    private Long id;
    /**
     * 学习计划ID
     */
    private Long planId;
    /**
     * 知识点ID
     */
    private Long knowledgePointId;
    /**
     * 第几天
     */
    private Integer dayIndex;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 完成状态：0-未完成 1-已完成 2-补交
     */
    private Integer completed;
    /**
     * 完成时间
     */
    private Date completionDate;
    /**
     *
     */
    private Date createDate;
    /**
     *
     */
    private Date updateDate;
    /**
     * 科目名称
     */
    private String subjectName;
    /**
     * 是否复习项 0-否 1-是
     */
    private Integer isReview;
    /**
     * 拆分部分（第几部分），用于跨天学习的知识点
     */
    private Integer splitPart;
    /**
     * 总部分数，用于跨天学习的知识点
     */
    private Integer totalParts;
    /**
     * 本次分配的时长（小时），用于跨天学习的知识点
     */
    private Integer splitHours;
}