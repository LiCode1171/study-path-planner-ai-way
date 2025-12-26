ALTER TABLE study_plan_item 
ADD COLUMN subject_name VARCHAR(100) COMMENT '科目名称',
ADD COLUMN is_review TINYINT DEFAULT 0 COMMENT '是否复习项 0-否 1-是';

ALTER TABLE study_plan 
ADD COLUMN total_subjects INT DEFAULT 0 COMMENT '总科目数',
ADD COLUMN completed_subjects INT DEFAULT 0 COMMENT '已完成科目数',
ADD COLUMN total_review_days INT DEFAULT 7 COMMENT '总复习天数',
ADD COLUMN overall_progress DECIMAL(5,2) DEFAULT 0.0 COMMENT '总体进度';