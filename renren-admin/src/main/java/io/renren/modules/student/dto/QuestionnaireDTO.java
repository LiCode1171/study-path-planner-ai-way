package io.renren.modules.student.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 学情诊断问卷DTO
 *
 *  @author Haili 695536881@qq.com and deepseek
 *  @since 1.0.0 2025-11-15
 */
@Data
@Schema(name = "学情诊断问卷")
public class QuestionnaireDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "招考类别")
    private String examCategory;

    @Schema(description = "目标院校")
    private String targetSchool;

    @Schema(description = "当前专业")
    private String currentMajor;

    @Schema(description = "目标专业")
    private String targetMajor;

    @Schema(description = "备考时间")
    private String preparationTime;

    @Schema(description = "每日学习时间")
    private String dailyStudyTime;

    @Schema(description = "科目水平评估")
    private Map<String, Integer> subjectLevels;

    // 转换备考时间为月数
    public Integer getPreparationMonths() {
        if (preparationTime == null) return null;
        switch (preparationTime) {
            case "3个月": return 3;
            case "6个月": return 6;
            case "9个月": return 9;
            case "12个月": return 12;
            default: return 3;
        }
    }

    // 转换每日学习时间为小时数（取中间值）
    public Integer getDailyStudyHours() {
        if (dailyStudyTime == null) return null;
        switch (dailyStudyTime) {
            case "1-2小时": return 2;
            case "2-4小时": return 3;
            case "4-6小时": return 5;
            case "6小时以上": return 7;
            default: return 3;
        }
    }

    // 确定公共课类型
    public String getPublicCourseType() {
        if (examCategory == null) return null;
        // 根据福建专升本规则，理工/医学/农林类考高数，其他考语文
        switch (examCategory) {
            case "理工类":
            case "医学类":
            case "农林生物医药类":
                return "高等数学";
            default:
                return "大学语文";
        }
    }
}