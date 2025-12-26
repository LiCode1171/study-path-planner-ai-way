package io.renren.modules.student.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 学习计划表
 *
 * @author Haili 695536881@qq.com
 * @since 1.0.0 2025-11-23
 */
@Data
@TableName("study_plan")
public class StudyPlanEntity {

    /**
     *
     */
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 计划名称
     */
    private String planName;
    /**
     * 总天数
     */
    private Integer totalDays;
    /**
     * 每日学习小时数
     */
    private Integer dailyHours;
    /**
     * 开始日期
     */
    private Date startDate;
    /**
     * 结束日期
     */
    private Date endDate;
    /**
     * 状态：0-未开始，1-进行中，2-已完成
     */
    private Integer status;
    /**
     *
     */
    private Date createDate;
    /**
     *
     */
    private Date updateDate;

    /**
     * 学习计划项列表
     * 非数据库字段 - 用于存储关联的计划项
     * TableField(exist = false) 这个注解告诉 MyBatis Plus 这个字段不存在于数据库表中
     */
    @TableField(exist = false)
    private List<StudyPlanItemEntity> planItems;

    /**
     * 总科目数
     */
    private Integer totalSubjects;
    /**
     * 已完成科目数
     */
    private Integer completedSubjects;
    /**
     * 总复习天数
     */
    private Integer totalReviewDays;
    /**
     * 总体进度
     */
    private BigDecimal overallProgress;
}