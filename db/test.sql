CREATE TABLE `user_profile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '关联用户ID',
  `exam_category` varchar(50) NOT NULL COMMENT '招考类别(理工类/经管类/医学类等)',
  `target_school` varchar(255) DEFAULT NULL COMMENT '目标院校',
  `current_major` varchar(255) DEFAULT NULL COMMENT '当前专业',
  `target_major` varchar(255) DEFAULT NULL COMMENT '目标专业',
  `preparation_months` int(11) DEFAULT NULL COMMENT '备考月数',
  `daily_study_time` int(11) DEFAULT NULL COMMENT '每日学习时间(小时)',
  `public_course_type` varchar(20) DEFAULT NULL COMMENT '公共基础课类型(大学语文/高等数学)',
  `create_date` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='用户专升本信息扩展表';

CREATE TABLE `user_subject_level` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `subject_type` varchar(20) NOT NULL COMMENT '科目类型(public/professional)',
  `subject_name` varchar(100) NOT NULL COMMENT '科目名称',
  `level` int(11) DEFAULT NULL COMMENT '基础水平1-5',
  `create_date` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='用户科目基础水平表';

CREATE TABLE `knowledge_point` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `exam_category` varchar(50) DEFAULT NULL COMMENT '招考类别',
  `subject_name` varchar(100) DEFAULT NULL COMMENT '科目名称',
  `chapter` varchar(255) DEFAULT NULL COMMENT '章节',
  `point_name` varchar(255) DEFAULT NULL COMMENT '知识点名称',
  `difficulty` int(11) DEFAULT NULL COMMENT '难度等级1-5',
  `estimated_hours` int(11) DEFAULT NULL COMMENT '预计学习时长(小时)',
  `weight` int(11) DEFAULT NULL COMMENT '考试权重1-10',
  `prerequisite_ids` varchar(500) DEFAULT NULL COMMENT '前置知识点ID',
  `description` text COMMENT '知识点描述',
  `create_date` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='知识点库';

CREATE TABLE `learning_resource` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(500) DEFAULT NULL COMMENT '资源标题',
  `url` varchar(1000) DEFAULT NULL COMMENT '资源链接',
  `resource_type` varchar(50) DEFAULT NULL COMMENT '类型(视频/文章/习题)',
  `exam_category` varchar(50) DEFAULT NULL COMMENT '适用招考类别',
  `subject_name` varchar(100) DEFAULT NULL COMMENT '适用科目',
  `knowledge_point_ids` varchar(500) DEFAULT NULL COMMENT '关联知识点',
  `difficulty` int(11) DEFAULT NULL COMMENT '难度等级',
  `source_platform` varchar(100) DEFAULT NULL COMMENT '来源平台',
  `description` text COMMENT '资源描述',
  `create_date` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='学习资源表';

CREATE TABLE `study_plan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `plan_name` varchar(255) DEFAULT NULL COMMENT '计划名称',
  `total_days` int(11) DEFAULT NULL COMMENT '总天数',
  `daily_hours` int(11) DEFAULT NULL COMMENT '每日学习小时数',
  `start_date` date DEFAULT NULL COMMENT '开始日期',
  `end_date` date DEFAULT NULL COMMENT '结束日期',
  `status` int(11) DEFAULT NULL COMMENT '状态：0-未开始，1-进行中，2-已完成',
  `create_date` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='学习计划表';

CREATE TABLE `study_plan_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `plan_id` bigint(20) NOT NULL COMMENT '学习计划ID',
  `knowledge_point_id` bigint(20) DEFAULT NULL COMMENT '知识点ID',
  `day_index` int(11) DEFAULT NULL COMMENT '第几天',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `completed` tinyint(1) DEFAULT '0' COMMENT '是否完成',
  `completion_date` datetime DEFAULT NULL COMMENT '完成时间',
  `create_date` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='学习计划项表';