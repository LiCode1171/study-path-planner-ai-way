package io.renren.modules.student.dao;

import io.renren.common.dao.BaseDao;
import io.renren.modules.student.entity.StudyPlanItemEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学习计划项表
 *
 * @author Haili 695536881@qq.com
 * @since 1.0.0 2025-11-28
 */
@Mapper
public interface StudyPlanItemDao extends BaseDao<StudyPlanItemEntity> {
	
}