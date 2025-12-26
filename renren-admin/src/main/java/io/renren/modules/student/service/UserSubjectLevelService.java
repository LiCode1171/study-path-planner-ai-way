package io.renren.modules.student.service;

import io.renren.common.service.CrudService;
import io.renren.modules.student.dto.UserSubjectLevelDTO;
import io.renren.modules.student.entity.UserSubjectLevelEntity;

import java.util.List;

/**
 * 用户科目基础水平表
 *
 * @author Haili 695536881@qq.com
 * @since 1.0.0 2025-11-02
 */
public interface UserSubjectLevelService extends CrudService<UserSubjectLevelEntity, UserSubjectLevelDTO> {

    /**
     * 根据用户ID删除科目水平记录（用于保持一个用户ID科目水平记录的唯一性）
     *  @author Haili 695536881@qq.com and deepseek
     *  @since 1.0.0 2025-11-15
     */
    void deleteByUserId(Long userId);

    /**
     * 根据用户ID查询科目水平记录
     *  @author Haili 695536881@qq.com and deepseek
     *  @since 1.0.0 2025-11-15
     */
    List<UserSubjectLevelEntity> getByUserId(Long userId);
}