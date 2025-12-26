package io.renren.modules.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.CrudServiceImpl;
import io.renren.modules.student.dao.KnowledgePointDao;
import io.renren.modules.student.dto.KnowledgePointDTO;
import io.renren.modules.student.entity.KnowledgePointEntity;
import io.renren.modules.student.service.KnowledgePointService;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 知识点库
 *
 * @author Haili 695536881@qq.com
 * @since 1.0.0 2025-11-02
 */
@Service
public class KnowledgePointServiceImpl extends CrudServiceImpl<KnowledgePointDao, KnowledgePointEntity, KnowledgePointDTO> implements KnowledgePointService {

    @Override
    public QueryWrapper<KnowledgePointEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<KnowledgePointEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StrUtil.isNotBlank(id), "id", id);

        return wrapper;
    }

    @Override
    public List<KnowledgePointEntity> listByExamCategory(String examCategory) {
        QueryWrapper<KnowledgePointEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("exam_category", examCategory)
                .orderByAsc("subject_name", "chapter", "id"); // 使用id排序，如果没有sort字段
        return baseDao.selectList(wrapper);
    }

    @Override
    public List<KnowledgePointEntity> listBySubject(String subjectName) {
        QueryWrapper<KnowledgePointEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("subject_name", subjectName)
                .orderByAsc("chapter", "id");
        return baseDao.selectList(wrapper);
    }

}