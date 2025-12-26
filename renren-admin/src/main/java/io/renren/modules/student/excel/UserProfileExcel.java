package io.renren.modules.student.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

import java.util.Date;

/**
 * 用户专升本信息扩展表
 *
 * @author Haili 695536881@qq.com
 * @since 1.0.0 2025-11-02
 */
@Data
public class UserProfileExcel {
    @ExcelProperty(value = "")
    private Long id;
    @ExcelProperty(value = "关联用户ID")
    private Long userId;
    @ExcelProperty(value = "招考类别(理工类/经管类/医学类等)")
    private String examCategory;
    @ExcelProperty(value = "目标院校")
    private String targetSchool;
    @ExcelProperty(value = "当前专业")
    private String currentMajor;
    @ExcelProperty(value = "目标专业")
    private String targetMajor;
    @ExcelProperty(value = "备考月数")
    private Integer preparationMonths;
    @ExcelProperty(value = "每日学习时间(小时)")
    private Integer dailyStudyTime;
    @ExcelProperty(value = "公共基础课类型(大学语文/高等数学)")
    private String publicCourseType;
    @ExcelProperty(value = "")
    private Date createDate;
    @ExcelProperty(value = "")
    private Date updateDate;

}