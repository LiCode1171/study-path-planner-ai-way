package io.renren.modules.student.dao;

import io.renren.common.dao.BaseDao;
import io.renren.modules.student.entity.UserSubjectLevelEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户科目基础水平表
 *
 * @author Haili 695536881@qq.com
 * @since 1.0.0 2025-11-02
 */
@Mapper
public interface UserSubjectLevelDao extends BaseDao<UserSubjectLevelEntity> {
	
}