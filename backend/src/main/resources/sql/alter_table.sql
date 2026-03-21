-- 修改 users 表的 avatar 字段类型，从 VARCHAR(500) 改为 LONGTEXT
USE starfire_mall;

ALTER TABLE users MODIFY COLUMN avatar LONGTEXT DEFAULT NULL COMMENT '头像';