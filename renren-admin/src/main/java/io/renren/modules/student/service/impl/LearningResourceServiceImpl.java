package io.renren.modules.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.CrudServiceImpl;
import io.renren.modules.student.dao.LearningResourceDao;
import io.renren.modules.student.dto.LearningResourceDTO;
import io.renren.modules.student.entity.LearningResourceEntity;
import io.renren.modules.student.service.LearningResourceService;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 学习资源表
 *
 * @author Haili 695536881@qq.com
 * @since 1.0.0 2025-11-02
 */
@Service
public class LearningResourceServiceImpl extends CrudServiceImpl<LearningResourceDao, LearningResourceEntity, LearningResourceDTO> implements LearningResourceService {

    @Override
    public QueryWrapper<LearningResourceEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<LearningResourceEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StrUtil.isNotBlank(id), "id", id);

        return wrapper;
    }


}