package io.renren.modules.student.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import java.math.BigDecimal;

/**
 * 学习计划表
 *
 * @author Haili 695536881@qq.com
 * @since 1.0.0 2025-11-23
 */
@Data
@Schema(name = "学习计划表")
public class StudyPlanDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @SchemaProperty(name = "")
    private Long id;

    @SchemaProperty(name = "用户ID")
    private Long userId;

    @SchemaProperty(name = "计划名称")
    private String planName;

    @SchemaProperty(name = "总天数")
    private Integer totalDays;

    @SchemaProperty(name = "每日学习小时数")
    private Integer dailyHours;

    @SchemaProperty(name = "开始日期")
    private Date startDate;

    @SchemaProperty(name = "结束日期")
    private Date endDate;

    @SchemaProperty(name = "状态：0-未开始，1-进行中，2-已完成")
    private Integer status;

    @SchemaProperty(name = "")
    private Date createDate;

    @SchemaProperty(name = "")
    private Date updateDate;

    @SchemaProperty(name = "总科目数")
    private Integer totalSubjects;

    @SchemaProperty(name = "已完成科目数")
    private Integer completedSubjects;

    @SchemaProperty(name = "总复习天数")
    private Integer totalReviewDays;

    @SchemaProperty(name = "总体进度")
    private BigDecimal overallProgress;


}
