package io.renren.modules.student.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

import java.util.Date;

/**
 * 学习资源表
 *
 * @author Haili 695536881@qq.com
 * @since 1.0.0 2025-11-02
 */
@Data
public class LearningResourceExcel {
    @ExcelProperty(value = "")
    private Long id;
    @ExcelProperty(value = "资源标题")
    private String title;
    @ExcelProperty(value = "资源链接")
    private String url;
    @ExcelProperty(value = "类型(视频/文章/习题)")
    private String resourceType;
    @ExcelProperty(value = "适用招考类别")
    private String examCategory;
    @ExcelProperty(value = "适用科目")
    private String subjectName;
    @ExcelProperty(value = "关联知识点")
    private String knowledgePointIds;
    @ExcelProperty(value = "难度等级")
    private Integer difficulty;
    @ExcelProperty(value = "来源平台")
    private String sourcePlatform;
    @ExcelProperty(value = "资源描述")
    private String description;
    @ExcelProperty(value = "")
    private Date createDate;
    @ExcelProperty(value = "")
    private Date updateDate;

}