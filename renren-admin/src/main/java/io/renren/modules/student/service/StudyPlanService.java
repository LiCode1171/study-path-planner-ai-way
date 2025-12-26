package io.renren.modules.student.service;

import io.renren.common.service.CrudService;
import io.renren.modules.student.dto.DashboardDTO;
import io.renren.modules.student.dto.StudyPlanDTO;
import io.renren.modules.student.entity.StudyPlanEntity;

/**
 * 学习计划表
 *
 * @author Haili 695536881@qq.com
 * @since 1.0.0 2025-11-23
 */
public interface StudyPlanService extends CrudService<StudyPlanEntity, StudyPlanDTO> {

    /**
     * 生成学习计划
     *
     *  @author Haili 695536881@qq.com and deepseek
     *  @since 1.0.0 2025-11-15
     */
    StudyPlanEntity generatePlan(Long userId);

    /**
     * 根据用户ID查询学习计划
     */
    StudyPlanEntity getByUserId(Long userId);

    /**
     * 获取用户Dashboard数据
     * @param userId 用户ID
     * @return Dashboard数据封装
     */
    DashboardDTO getDashboardData(Long userId);
}