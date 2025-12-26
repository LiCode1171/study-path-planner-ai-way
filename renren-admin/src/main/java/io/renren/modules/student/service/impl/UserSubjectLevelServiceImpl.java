package io.renren.modules.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.CrudServiceImpl;
import io.renren.modules.student.dao.UserSubjectLevelDao;
import io.renren.modules.student.dto.UserSubjectLevelDTO;
import io.renren.modules.student.entity.UserSubjectLevelEntity;
import io.renren.modules.student.service.UserSubjectLevelService;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 用户科目基础水平表
 *
 * @author Haili 695536881@qq.com
 * @since 1.0.0 2025-11-02
 */
@Service
public class UserSubjectLevelServiceImpl extends CrudServiceImpl<UserSubjectLevelDao, UserSubjectLevelEntity, UserSubjectLevelDTO> implements UserSubjectLevelService {

    @Override
    public QueryWrapper<UserSubjectLevelEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<UserSubjectLevelEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StrUtil.isNotBlank(id), "id", id);

        return wrapper;
    }

    @Override
    public void deleteByUserId(Long userId) {
        QueryWrapper<UserSubjectLevelEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        baseDao.delete(wrapper);
    }
    @Override
    public List<UserSubjectLevelEntity> getByUserId(Long userId) {
        QueryWrapper<UserSubjectLevelEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return baseDao.selectList(wrapper);
    }

}