package io.renren.modules.student.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 用户科目基础水平表
 *
 * @author Haili 695536881@qq.com
 * @since 1.0.0 2025-11-02
 */
@Data
@TableName("user_subject_level")
public class UserSubjectLevelEntity {

    /**
     * 
     */
	private Long id;
    /**
     * 用户ID
     */
	private Long userId;
    /**
     * 科目类型(public/professional)
     */
	private String subjectType;
    /**
     * 科目名称
     */
	private String subjectName;
    /**
     * 基础水平1-5
     */
	private Integer level;
    /**
     * 
     */
	private Date createDate;
    /**
     * 
     */
	private Date updateDate;
}