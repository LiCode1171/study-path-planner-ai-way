package io.renren.modules.student.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

import java.util.Date;

/**
 * 学习计划项表
 *
 * @author Haili 695536881@qq.com
 * @since 1.0.0 2025-11-28
 */
@Data
public class StudyPlanItemExcel {
    @ExcelProperty(value = "")
    private Long id;
    @ExcelProperty(value = "学习计划ID")
    private Long planId;
    @ExcelProperty(value = "知识点ID")
    private Long knowledgePointId;
    @ExcelProperty(value = "第几天")
    private Integer dayIndex;
    @ExcelProperty(value = "排序")
    private Integer sort;
    @ExcelProperty(value = "完成状态 0-未完成 1-已完成 2-补交")
    private Integer completed;
    @ExcelProperty(value = "完成时间")
    private Date completionDate;
    @ExcelProperty(value = "")
    private Date createDate;
    @ExcelProperty(value = "")
    private Date updateDate;
    @ExcelProperty(value = "科目名称")
    private String subjectName;
    @ExcelProperty(value = "是否复习项 0-否 1-是")
    private Integer isReview;
    @ExcelProperty(value = "拆分部分（第几部分），用于跨天学习的知识点")
    private Integer splitPart;
    @ExcelProperty(value = "总部分数，用于跨天学习的知识点")
    private Integer totalParts;
    @ExcelProperty(value = "本次分配的时长（小时），用于跨天学习的知识点")
    private Integer splitHours;

}