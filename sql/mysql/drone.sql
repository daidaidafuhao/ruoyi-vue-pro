-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 创建无人机表
CREATE TABLE `drone` (
    `drone_code` varchar(50) NOT NULL COMMENT '无人机编号（主键）',
    `drone_name` varchar(100) DEFAULT NULL COMMENT '无人机名称',
    `model` varchar(50) DEFAULT NULL COMMENT '无人机型号',
    `serial_number` varchar(100) DEFAULT NULL COMMENT '序列号',
    `manufacturer` varchar(100) DEFAULT NULL COMMENT '制造商',
    `status` tinyint DEFAULT '0' COMMENT '状态（0-待机 1-飞行中 2-返航中 3-充电中 4-维护中 5-故障 6-离线）',
    `battery_level` int DEFAULT NULL COMMENT '电池电量百分比（0-100）',
    `flight_time` int DEFAULT 0 COMMENT '累计飞行时间（分钟）',
    `max_payload` decimal(8,2) DEFAULT NULL COMMENT '最大载重（kg）',
    `max_flight_time` int DEFAULT NULL COMMENT '最大飞行时间（分钟）',
    `longitude` decimal(12,8) DEFAULT NULL COMMENT '当前经度',
    `latitude` decimal(12,8) DEFAULT NULL COMMENT '当前纬度',
    `altitude` decimal(8,2) DEFAULT NULL COMMENT '当前海拔高度（米）',
    `speed` decimal(8,2) DEFAULT NULL COMMENT '当前速度（m/s）',
    `heading` decimal(6,2) DEFAULT NULL COMMENT '航向角度（0-360度）',
    `home_longitude` decimal(12,8) DEFAULT NULL COMMENT '起飞点经度',
    `home_latitude` decimal(12,8) DEFAULT NULL COMMENT '起飞点纬度',
    `home_altitude` decimal(8,2) DEFAULT NULL COMMENT '起飞点海拔高度（米）',
    `last_flight_time` datetime DEFAULT NULL COMMENT '最后飞行时间',
    `last_landing_time` datetime DEFAULT NULL COMMENT '最后降落时间',
    `last_maintenance_time` datetime DEFAULT NULL COMMENT '最后维护时间',
    `next_maintenance_time` datetime DEFAULT NULL COMMENT '下次维护时间',
    `current_mission_id` varchar(50) DEFAULT NULL COMMENT '当前任务ID',
    `current_order_id` bigint DEFAULT NULL COMMENT '当前运送订单编号',
    `current_order_no` varchar(32) DEFAULT NULL COMMENT '当前运送订单流水号',
    `source_cabinet_code` varchar(20) DEFAULT NULL COMMENT '起送柜子编号',
    `target_cabinet_code` varchar(20) DEFAULT NULL COMMENT '目标柜子编号',
    `operator` varchar(64) DEFAULT NULL COMMENT '当前操作员',
    `error_message` varchar(500) DEFAULT NULL COMMENT '故障信息',
    `last_error_time` datetime DEFAULT NULL COMMENT '最后故障时间',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `creator` varchar(64) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) DEFAULT NULL COMMENT '更新者',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint DEFAULT 0 COMMENT '租户编号',
    `dept_id` bigint DEFAULT NULL COMMENT '部门编号',
    PRIMARY KEY (`drone_code`),
    KEY `idx_serial_number` (`serial_number`),
    KEY `idx_status` (`status`),
    KEY `idx_battery_level` (`battery_level`),
    KEY `idx_location` (`longitude`, `latitude`),
    KEY `idx_current_order` (`current_order_id`),
    KEY `idx_current_order_no` (`current_order_no`),
    KEY `idx_source_cabinet` (`source_cabinet_code`),
    KEY `idx_target_cabinet` (`target_cabinet_code`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='无人机表';

-- 创建无人机飞行记录表
CREATE TABLE `drone_flight_log` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `drone_code` varchar(50) NOT NULL COMMENT '无人机编号',
    `mission_id` varchar(50) DEFAULT NULL COMMENT '任务ID',
    `order_id` bigint DEFAULT NULL COMMENT '关联订单编号',
    `order_no` varchar(32) DEFAULT NULL COMMENT '关联订单流水号',
    `source_cabinet_code` varchar(20) DEFAULT NULL COMMENT '起送柜子编号',
    `target_cabinet_code` varchar(20) DEFAULT NULL COMMENT '目标柜子编号',
    `flight_type` varchar(20) NOT NULL COMMENT '飞行类型（delivery-配送 patrol-巡逻 test-测试 manual-手动）',
    `takeoff_time` datetime NOT NULL COMMENT '起飞时间',
    `landing_time` datetime DEFAULT NULL COMMENT '降落时间',
    `flight_duration` int DEFAULT NULL COMMENT '飞行时长（分钟）',
    `takeoff_longitude` decimal(12,8) DEFAULT NULL COMMENT '起飞点经度',
    `takeoff_latitude` decimal(12,8) DEFAULT NULL COMMENT '起飞点纬度',
    `takeoff_altitude` decimal(8,2) DEFAULT NULL COMMENT '起飞点海拔（米）',
    `landing_longitude` decimal(12,8) DEFAULT NULL COMMENT '降落点经度',
    `landing_latitude` decimal(12,8) DEFAULT NULL COMMENT '降落点纬度',
    `landing_altitude` decimal(8,2) DEFAULT NULL COMMENT '降落点海拔（米）',
    `max_altitude` decimal(8,2) DEFAULT NULL COMMENT '最大飞行高度（米）',
    `max_speed` decimal(8,2) DEFAULT NULL COMMENT '最大飞行速度（m/s）',
    `distance_traveled` decimal(10,2) DEFAULT NULL COMMENT '飞行距离（米）',
    `battery_start` int DEFAULT NULL COMMENT '起飞时电量（%）',
    `battery_end` int DEFAULT NULL COMMENT '降落时电量（%）',
    `status` tinyint NOT NULL DEFAULT '0' COMMENT '飞行状态（0-进行中 1-正常完成 2-异常结束 3-紧急降落）',
    `operator` varchar(64) DEFAULT NULL COMMENT '操作员',
    `error_message` varchar(500) DEFAULT NULL COMMENT '异常信息',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `creator` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    KEY `idx_drone_code` (`drone_code`),
    KEY `idx_mission_id` (`mission_id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_source_cabinet_flight` (`source_cabinet_code`),
    KEY `idx_target_cabinet_flight` (`target_cabinet_code`),
    KEY `idx_takeoff_time` (`takeoff_time`),
    KEY `idx_status` (`status`),
    KEY `idx_tenant_id` (`tenant_id`),
    CONSTRAINT `fk_drone_flight_log_drone` FOREIGN KEY (`drone_code`) REFERENCES `drone` (`drone_code`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='无人机飞行记录表';

-- 创建无人机位置轨迹表
CREATE TABLE `drone_position_track` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `drone_code` varchar(50) NOT NULL COMMENT '无人机编号',
    `flight_log_id` bigint DEFAULT NULL COMMENT '飞行记录ID',
    `longitude` decimal(12,8) NOT NULL COMMENT '经度',
    `latitude` decimal(12,8) NOT NULL COMMENT '纬度',
    `altitude` decimal(8,2) DEFAULT NULL COMMENT '海拔高度（米）',
    `speed` decimal(8,2) DEFAULT NULL COMMENT '速度（m/s）',
    `heading` decimal(6,2) DEFAULT NULL COMMENT '航向角度（0-360度）',
    `battery_level` int DEFAULT NULL COMMENT '电池电量（%）',
    `timestamp` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '时间戳',
    `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    KEY `idx_drone_code` (`drone_code`),
    KEY `idx_flight_log_id` (`flight_log_id`),
    KEY `idx_timestamp` (`timestamp`),
    KEY `idx_location` (`longitude`, `latitude`),
    KEY `idx_tenant_id` (`tenant_id`),
    CONSTRAINT `fk_drone_position_track_drone` FOREIGN KEY (`drone_code`) REFERENCES `drone` (`drone_code`) ON DELETE CASCADE,
    CONSTRAINT `fk_drone_position_track_flight` FOREIGN KEY (`flight_log_id`) REFERENCES `drone_flight_log` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='无人机位置轨迹表';

-- 插入测试数据
INSERT INTO `drone` 
(`drone_code`, `drone_name`, `model`, `serial_number`, `manufacturer`, `status`, `battery_level`, `max_payload`, `max_flight_time`, `longitude`, `latitude`, `altitude`, `home_longitude`, `home_latitude`, `home_altitude`, `current_order_id`, `current_order_no`, `source_cabinet_code`, `target_cabinet_code`, `remark`, `tenant_id`, `dept_id`) 
VALUES 
('DRONE001', 'DJI配送无人机01', 'DJI Matrice 300 RTK', 'DJ001-20240101-001', 'DJI', 1, 85, 2.70, 55, 120.123456, 30.123456, 50.0, 120.123456, 30.123456, 50.0, 248, '202407140001', 'DC001', 'DC002', '主力配送无人机，正在从DC001配送到DC002，订单248', 1, 1),
('DRONE002', 'DJI配送无人机02', 'DJI Matrice 300 RTK', 'DJ001-20240101-002', 'DJI', 3, 95, 2.70, 55, 120.234567, 30.234567, 0.0, 120.234567, 30.234567, 50.0, NULL, NULL, NULL, NULL, '备用配送无人机', 1, 1),
('DRONE003', '巡逻无人机01', 'DJI Mini 3 Pro', 'DJ002-20240102-001', 'DJI', 0, 78, 0.25, 34, 120.345678, 30.345678, 100.0, 120.345678, 30.345678, 50.0, NULL, NULL, NULL, NULL, '巡逻专用无人机', 1, 1);

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1; 