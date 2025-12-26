package io.renren.modules.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.CrudServiceImpl;
import io.renren.modules.student.dao.StudyPlanItemDao;
import io.renren.modules.student.dto.StudyPlanItemDTO;
import io.renren.modules.student.entity.StudyPlanItemEntity;
import io.renren.modules.student.service.StudyPlanItemService;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 学习计划项表
 *
 * @author Haili 695536881@qq.com
 * @since 1.0.0 2025-11-02
 */
@Service
public class StudyPlanItemServiceImpl extends CrudServiceImpl<StudyPlanItemDao, StudyPlanItemEntity, StudyPlanItemDTO> implements StudyPlanItemService {

    private static final Logger logger = LoggerFactory.getLogger(StudyPlanItemServiceImpl.class);

    @Override
    public QueryWrapper<StudyPlanItemEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<StudyPlanItemEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StrUtil.isNotBlank(id), "id", id);

        return wrapper;
    }
    /**
     * 根据学习计划ID删除对应的学习计划项
     * @param planId 学习计划ID
     */
    @Override
    public void deleteByPlanId(Long planId) {
        QueryWrapper<StudyPlanItemEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("plan_id", planId);
        baseDao.delete(wrapper);
    }

    @Override
    public List<StudyPlanItemEntity> getByPlanId(Long planId) {
        QueryWrapper<StudyPlanItemEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("plan_id", planId)
                .orderByAsc("day_index", "sort");
        return baseDao.selectList(wrapper);
    }

    @Override
    public boolean saveBatch(List<StudyPlanItemDTO> planItemDTOs) {
        if (planItemDTOs == null || planItemDTOs.isEmpty()) {
            return true;
        }

        try {
            // 方案三：直接循环保存（最简单可靠）
            int successCount = 0;
            for (StudyPlanItemDTO dto : planItemDTOs) {
                try {
                    this.save(dto);
                    successCount++;
                } catch (Exception e) {
                    logger.error("保存单个学习计划项失败: {}", dto, e);
                }
            }

            logger.info("批量保存学习计划项完成，成功: {}，总数: {}", successCount, planItemDTOs.size());
            return successCount == planItemDTOs.size();

        } catch (Exception e) {
            logger.error("批量保存学习计划项失败", e);
            return false;
        }
    }
}