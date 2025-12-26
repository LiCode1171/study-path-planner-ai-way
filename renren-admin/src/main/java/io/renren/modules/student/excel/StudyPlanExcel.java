package io.renren.modules.student.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 学习计划表
 *
 * @author Haili 695536881@qq.com
 * @since 1.0.0 2025-11-23
 */
@Data
public class StudyPlanExcel {
    @ExcelProperty(value = "")
    private Long id;
    @ExcelProperty(value = "用户ID")
    private Long userId;
    @ExcelProperty(value = "计划名称")
    private String planName;
    @ExcelProperty(value = "总天数")
    private Integer totalDays;
    @ExcelProperty(value = "每日学习小时数")
    private Integer dailyHours;
    @ExcelProperty(value = "开始日期")
    private Date startDate;
    @ExcelProperty(value = "结束日期")
    private Date endDate;
    @ExcelProperty(value = "状态：0-未开始，1-进行中，2-已完成")
    private Integer status;
    @ExcelProperty(value = "")
    private Date createDate;
    @ExcelProperty(value = "")
    private Date updateDate;
    @ExcelProperty(value = "总科目数")
    private Integer totalSubjects;
    @ExcelProperty(value = "已完成科目数")
    private Integer completedSubjects;
    @ExcelProperty(value = "总复习天数")
    private Integer totalReviewDays;
    @ExcelProperty(value = "总体进度")
    private BigDecimal overallProgress;

}