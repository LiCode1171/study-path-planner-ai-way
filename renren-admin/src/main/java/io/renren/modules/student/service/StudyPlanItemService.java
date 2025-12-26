package io.renren.modules.student.service;

import io.renren.common.service.CrudService;
import io.renren.modules.student.dto.StudyPlanItemDTO;
import io.renren.modules.student.entity.StudyPlanItemEntity;

import java.util.List;

/**
 * 学习计划项表
 *
 * @author Haili 695536881@qq.com
 * @since 1.0.0 2025-11-02
 */
public interface StudyPlanItemService extends CrudService<StudyPlanItemEntity, StudyPlanItemDTO> {

    /**
     * 根据计划ID删除
     *
     *  @author Haili 695536881@qq.com and deepseek
     *  @since 1.0.0 2025-11-23
     */
    void deleteByPlanId(Long planId);

    /**
     * 根据计划ID查询计划项
     */
    List<StudyPlanItemEntity> getByPlanId(Long planId);

    /**
     * 批量保存学习计划项
     */
    boolean saveBatch(List<StudyPlanItemDTO> planItemDTOs);
}