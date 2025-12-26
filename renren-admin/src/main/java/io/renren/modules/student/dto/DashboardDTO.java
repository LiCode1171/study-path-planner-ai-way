package io.renren.modules.student.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Dashboard数据DTO
 *
 * @author Haili 695536881@qq.com and kimi
 * @since 1.0.0 2025-11-29
 */
@Data
@Schema(name = "Dashboard数据")
public class DashboardDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "计划名称")
    private String planName;

    @Schema(description = "总天数")
    private Integer totalDays;

    @Schema(description = "已完成任务数")
    private Integer completedTasks;

    @Schema(description = "总任务数")
    private Integer totalTasks;

    @Schema(description = "剩余天数")
    private Integer remainingDays;

    @Schema(description = "整体进度百分比")
    private Integer overallProgress;

    @Schema(description = "今日任务列表")
    private List<StudyPlanItemDTO> todayTasks;

    @Schema(description = "所有任务列表")
    private List<StudyPlanItemDTO> allTasks;

    @Schema(description = "开始日期")
    private Date startDate;

    @Schema(description = "结束日期")
    private Date endDate;
}