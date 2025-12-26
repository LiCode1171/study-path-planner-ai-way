package io.renren.modules.student.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 用户专升本信息扩展表
 *
 * @author Haili 695536881@qq.com
 * @since 1.0.0 2025-11-02
 */
@Data
@TableName("user_profile")
public class UserProfileEntity {

    /**
     * 
     */
	private Long id;
    /**
     * 关联用户ID
     */
	private Long userId;
    /**
     * 招考类别(理工类/经管类/医学类等)
     */
	private String examCategory;
    /**
     * 目标院校
     */
	private String targetSchool;
    /**
     * 当前专业
     */
	private String currentMajor;
    /**
     * 目标专业
     */
	private String targetMajor;
    /**
     * 备考月数
     */
	private Integer preparationMonths;
    /**
     * 每日学习时间(小时)
     */
	private Integer dailyStudyTime;
    /**
     * 公共基础课类型(大学语文/高等数学)
     */
	private String publicCourseType;
    /**
     * 
     */
	private Date createDate;
    /**
     * 
     */
	private Date updateDate;
}