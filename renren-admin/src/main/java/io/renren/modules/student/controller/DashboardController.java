package io.renren.modules.student.controller;

import io.renren.common.utils.Result;
import io.renren.modules.student.dto.DashboardDTO;
import io.renren.modules.student.service.StudyPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 学习Dashboard控制器
 *
 * @author Haili 695536881@qq.com and kimi
 * @since 1.0.0 2025-11-29
 */
@RestController
@RequestMapping("student/dashboard")
@Tag(name = "学习Dashboard")
public class DashboardController {

    @Autowired
    private StudyPlanService studyPlanService;

    /**
     * 获取用户Dashboard数据
     *
     * @param userId 用户ID
     * @return Dashboard数据
     */
    @GetMapping("/data")
    @Operation(summary = "获取Dashboard数据")
    public Result<DashboardDTO> getDashboardData(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId) {
        
        DashboardDTO data = studyPlanService.getDashboardData(userId);
        
        if (data == null) {
            return new Result<DashboardDTO>().error("用户没有学习计划");
        }
        
        return new Result<DashboardDTO>().ok(data);
    }
}