-- 添加跨天学习相关字段
ALTER TABLE study_plan_item 
ADD COLUMN split_part INT DEFAULT 1 COMMENT '拆分部分（第几部分），用于跨天学习的知识点',
ADD COLUMN total_parts INT DEFAULT 1 COMMENT '总部分数，用于跨天学习的知识点',
ADD COLUMN split_hours INT COMMENT '本次分配的时长（小时），用于跨天学习的知识点';