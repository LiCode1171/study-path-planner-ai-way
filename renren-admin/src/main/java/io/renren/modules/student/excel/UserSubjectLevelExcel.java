package io.renren.modules.student.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

import java.util.Date;

/**
 * 用户科目基础水平表
 *
 * @author Haili 695536881@qq.com
 * @since 1.0.0 2025-11-02
 */
@Data
public class UserSubjectLevelExcel {
    @ExcelProperty(value = "")
    private Long id;
    @ExcelProperty(value = "用户ID")
    private Long userId;
    @ExcelProperty(value = "科目类型(public/professional)")
    private String subjectType;
    @ExcelProperty(value = "科目名称")
    private String subjectName;
    @ExcelProperty(value = "基础水平1-5")
    private Integer level;
    @ExcelProperty(value = "")
    private Date createDate;
    @ExcelProperty(value = "")
    private Date updateDate;

}