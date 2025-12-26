// io/renren/modules/student/controller/QuestionnaireController.java
package io.renren.modules.student.controller;

import io.renren.common.utils.Result;
import io.renren.modules.student.dto.QuestionnaireDTO;
import io.renren.modules.student.dto.UserProfileDTO;
import io.renren.modules.student.dto.UserSubjectLevelDTO;
import io.renren.modules.student.entity.StudyPlanEntity;
import io.renren.modules.student.entity.UserProfileEntity;
import io.renren.modules.student.service.StudyPlanService;
import io.renren.modules.student.service.UserProfileService;
import io.renren.modules.student.service.UserSubjectLevelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学情诊断问卷控制器
 *
 *  @author Haili 695536881@qq.com and deepseek
 *  @since 1.0.0 2025-11-15
 */
@RestController
@RequestMapping("student/questionnaire")
@Tag(name = "学情诊断问卷")
// 移除了 @Slf4j - 由全局异常处理器统一处理日志
public class QuestionnaireController {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserSubjectLevelService userSubjectLevelService;

    @Autowired
    private StudyPlanService studyPlanService;

    @PostMapping("submit")
    @Operation(summary = "提交问卷")
    public Result submitQuestionnaire(@RequestBody QuestionnaireDTO dto, HttpServletRequest request) {
        // TODO: 从token获取真实用户ID，目前先用测试用户
        Long userId = 1L; // 测试用户ID

        // 1. 保存用户画像
        UserProfileEntity profile = saveUserProfile(dto, userId);

        // 2. 保存科目水平数据
        saveSubjectLevels(dto, userId);

        // 3. 生成学习计划
        StudyPlanEntity studyPlan = studyPlanService.generatePlan(userId);

        // 创建响应数据Map
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("profile", profile);
        responseData.put("studyPlan", studyPlan);
        responseData.put("message", "学情诊断完成，学习计划已生成");

        // 使用正确的Result构造方式
        return new Result<Map<String, Object>>().ok(responseData);
    }

    private UserProfileEntity saveUserProfile(QuestionnaireDTO dto, Long userId) {
        // 先查询是否已存在该用户的画像
        UserProfileEntity existingProfile = userProfileService.getByUserId(userId);

        // 修正：创建DTO而不是Entity，然后保存
        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setUserId(userId);
        profileDTO.setExamCategory(dto.getExamCategory());
        profileDTO.setTargetSchool(dto.getTargetSchool());
        profileDTO.setCurrentMajor(dto.getCurrentMajor());
        profileDTO.setTargetMajor(dto.getTargetMajor());
        profileDTO.setPreparationMonths(dto.getPreparationMonths());
        profileDTO.setDailyStudyTime(dto.getDailyStudyHours());
        profileDTO.setPublicCourseType(dto.getPublicCourseType());

        if (existingProfile != null) {
            // 存在则更新：设置ID用于更新
            profileDTO.setId(existingProfile.getId());
            userProfileService.update(profileDTO);
        } else {
            // 不存在则保存DTO，然后获取Entity
            userProfileService.save(profileDTO);
        }

        // 如果需要返回最新的Entity，可以通过ID查询
        return userProfileService.getByUserId(userId);
    }

    private void saveSubjectLevels(QuestionnaireDTO dto, Long userId) {
        if (dto.getSubjectLevels() == null) return;

        // 先删除该用户的所有科目水平记录
        userSubjectLevelService.deleteByUserId(userId);

        List<UserSubjectLevelDTO> subjectLevelDTOs = new ArrayList<>();

        dto.getSubjectLevels().forEach((subjectName, level) -> {
            UserSubjectLevelDTO subjectLevelDTO = new UserSubjectLevelDTO();
            subjectLevelDTO.setUserId(userId);
            subjectLevelDTO.setSubjectName(subjectName);
            subjectLevelDTO.setLevel(level);

            // 设置科目类型
            if (subjectName.equals(getProfessionalSubjectName(dto.getExamCategory()))) {
                subjectLevelDTO.setSubjectType("professional");
            } else {
                subjectLevelDTO.setSubjectType("public");
            }

            subjectLevelDTOs.add(subjectLevelDTO);
        });

        // 修正：使用循环单个保存，因为可能没有saveBatch方法
        for (UserSubjectLevelDTO dtoItem : subjectLevelDTOs) {
            userSubjectLevelService.save(dtoItem);
        }
    }

    private String getProfessionalSubjectName(String examCategory) {
        switch (examCategory) {
            case "理工类": return "信息技术基础";
            case "经管类": return "经济学与管理学基础";
            case "医学类": return "医学基础";
            case "农林生物医药类": return "化学基础";
            case "文史哲法类": return "文史基础";
            case "教育类": return "教育理论基础";
            case "艺术类": return "艺术基础";
            default: return "专业基础课";
        }
    }
}