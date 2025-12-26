package io.renren.modules.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.CrudServiceImpl;
import io.renren.modules.student.dao.UserProfileDao;
import io.renren.modules.student.dto.UserProfileDTO;
import io.renren.modules.student.entity.UserProfileEntity;
import io.renren.modules.student.service.UserProfileService;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 用户专升本信息扩展表
 *  @author Haili 695536881@qq.com and deepseek
 *  @since 1.0.0 2025-11-15
 */
@Service
public class UserProfileServiceImpl extends CrudServiceImpl<UserProfileDao, UserProfileEntity, UserProfileDTO> implements UserProfileService {

    @Override
    public QueryWrapper<UserProfileEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<UserProfileEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StrUtil.isNotBlank(id), "id", id);

        return wrapper;
    }

    @Override
    public UserProfileEntity getByUserId(Long userId) {
        return baseDao.selectOne(new QueryWrapper<UserProfileEntity>().eq("user_id", userId));
    }
}