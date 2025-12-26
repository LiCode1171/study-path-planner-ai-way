package io.renren.modules.student.service;

import io.renren.common.service.CrudService;
import io.renren.modules.student.dto.KnowledgePointDTO;
import io.renren.modules.student.entity.KnowledgePointEntity;

import java.util.List;

/**
 * 知识点库
 *
 * @author Haili 695536881@qq.com
 * @since 1.0.0 2025-11-02
 */
public interface KnowledgePointService extends CrudService<KnowledgePointEntity, KnowledgePointDTO> {

    /**
     * 根据招考类别查询知识点
     * @author Haili 695536881@qq.com and deepseek
     * @since 1.0.0 2025-11-22
     */
    List<KnowledgePointEntity> listByExamCategory(String examCategory);

    /**
     * 根据科目名称查询知识点
     * @author Haili 695536881@qq.com and deepseek
     * @since 1.0.0 2025-11-22
     */
    List<KnowledgePointEntity> listBySubject(String subjectName);
}