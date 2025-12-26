package io.renren.modules.student.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

import java.util.Date;

/**
 * 知识点库
 *
 * @author Haili 695536881@qq.com
 * @since 1.0.0 2025-11-02
 */
@Data
public class KnowledgePointExcel {
    @ExcelProperty(value = "")
    private Long id;
    @ExcelProperty(value = "招考类别")
    private String examCategory;
    @ExcelProperty(value = "科目名称")
    private String subjectName;
    @ExcelProperty(value = "章节")
    private String chapter;
    @ExcelProperty(value = "知识点名称")
    private String pointName;
    @ExcelProperty(value = "难度等级1-5")
    private Integer difficulty;
    @ExcelProperty(value = "预计学习时长(小时)")
    private Integer estimatedHours;
    @ExcelProperty(value = "考试权重1-10")
    private Integer weight;
    @ExcelProperty(value = "前置知识点ID")
    private String prerequisiteIds;
    @ExcelProperty(value = "知识点描述")
    private String description;
    @ExcelProperty(value = "")
    private Date createDate;
    @ExcelProperty(value = "")
    private Date updateDate;

}