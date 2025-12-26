package io.renren.modules.student.service;

import io.renren.common.service.CrudService;
import io.renren.modules.student.dto.UserProfileDTO;
import io.renren.modules.student.entity.UserProfileEntity;

/**
 * 用户专升本信息扩展表
 *
 * @author Haili 695536881@qq.com
 * @since 1.0.0 2025-11-02
 */
public interface UserProfileService extends CrudService<UserProfileEntity, UserProfileDTO> {

    /**
     * 根据用户ID获取用户画像
     *  @author Haili 695536881@qq.com and deepseek
     *  @since 1.0.0 2025-11-15
     */
    UserProfileEntity getByUserId(Long userId);
}