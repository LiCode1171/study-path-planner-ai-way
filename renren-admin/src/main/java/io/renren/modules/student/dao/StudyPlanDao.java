package io.renren.modules.student.dao;

import io.renren.common.dao.BaseDao;
import io.renren.modules.student.entity.StudyPlanEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学习计划表
 *
 * @author Haili 695536881@qq.com
 * @since 1.0.0 2025-11-23
 */
@Mapper
public interface StudyPlanDao extends BaseDao<StudyPlanEntity> {
	
}