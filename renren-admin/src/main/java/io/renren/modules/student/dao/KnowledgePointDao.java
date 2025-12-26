package io.renren.modules.student.dao;

import io.renren.common.dao.BaseDao;
import io.renren.modules.student.entity.KnowledgePointEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 知识点库
 *
 * @author Haili 695536881@qq.com
 * @since 1.0.0 2025-11-02
 */
@Mapper
public interface KnowledgePointDao extends BaseDao<KnowledgePointEntity> {
	
}