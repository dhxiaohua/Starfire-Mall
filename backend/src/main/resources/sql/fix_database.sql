-- 修复 users 表的 avatar 字段类型
USE starfire_mall;

-- 修改 avatar 字段为 LONGTEXT
ALTER TABLE users MODIFY COLUMN avatar LONGTEXT DEFAULT NULL COMMENT '头像';