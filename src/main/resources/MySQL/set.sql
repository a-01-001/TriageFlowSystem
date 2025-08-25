CREATE DATABASE IF NOT EXISTS TriageFlowSystem CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE TriageFlowSystem;

-- 表1：患者基本情况
CREATE TABLE `patients` (
                            `patient_id` INT AUTO_INCREMENT PRIMARY KEY,
                            `name` VARCHAR(100) NOT NULL,
                            `gender` ENUM('Male', 'Female', 'Other') NOT NULL,
                            `age` INT NOT NULL,
                            `address` TEXT,
                            `phone` VARCHAR(20),
                            `is_fasting` BOOLEAN DEFAULT FALSE COMMENT '当前是否空腹状态',
                            `arrival_time` DATETIME NOT NULL COMMENT '患者到达时间',
                            `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 表3：检查项目详情
CREATE TABLE `medical_exams` (
                                 `exam_id` INT AUTO_INCREMENT PRIMARY KEY,
                                 `exam_name` VARCHAR(100) NOT NULL,
                                 `requires_fasting` BOOLEAN DEFAULT FALSE COMMENT '该项目是否需要空腹',
                                 `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 表4：检查仪器详情
CREATE TABLE `medical_devices` (
                                   `device_id`     INT AUTO_INCREMENT PRIMARY KEY,
                                   `device_name` VARCHAR(100) NOT NULL,
                                   `quantity`    INT DEFAULT 1 COMMENT '可用数量',
                                   `location`    VARCHAR(200),
                                   `created_at`  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 辅助表：仪器能检查的项目关联表
CREATE TABLE `device_exam_capabilities` (
                                            `device_id` INT NOT NULL,
                                            `exam_id` INT NOT NULL,
                                            `duration_minutes` INT NOT NULL COMMENT '该仪器做此项目的检查时间（分钟）',
                                            PRIMARY KEY (`device_id`, `exam_id`),
                                            FOREIGN KEY (`device_id`) REFERENCES `medical_devices`(`device_id`) ON DELETE CASCADE,
                                            FOREIGN KEY (`exam_id`) REFERENCES `medical_exams`(`exam_id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 辅助表：仪器工作时间段表
CREATE TABLE `device_working_schedules` (
                                            `schedule_id` INT AUTO_INCREMENT PRIMARY KEY,
                                            `device_id` INT NOT NULL,
                                            `day_of_week` TINYINT NOT NULL COMMENT '1-7表示周一到周日',
                                            `start_time` TIME NOT NULL,
                                            `end_time` TIME NOT NULL,
                                            `is_working` BOOLEAN DEFAULT TRUE COMMENT '是否工作时间',
                                            FOREIGN KEY (`device_id`) REFERENCES `medical_devices`(`device_id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 表2：患者的检查项目关联表
CREATE TABLE `patient_exam_assignments` (
                                            `assignment_id` INT AUTO_INCREMENT PRIMARY KEY,
                                            `patient_id` INT NOT NULL,
                                            `exam_id` INT NOT NULL,
                                            `status` ENUM('Pending', 'Scheduled', 'In Progress', 'Completed', 'Cancelled') DEFAULT 'Pending',
                                            `priority` TINYINT DEFAULT 0 COMMENT '优先级,对患者进行综合评分',
                                            `scheduled_start_time` DATETIME COMMENT '计划开始时间',
                                            `scheduled_end_time` DATETIME COMMENT '计划结束时间',
                                            `actual_start_time` DATETIME COMMENT '实际开始时间',
                                            `actual_end_time` DATETIME COMMENT '实际结束时间',
                                            `assigned_device_id` INT COMMENT '实际分配的仪器ID',
                                            `waiting_time_minutes` INT DEFAULT 0 COMMENT '等待时间（分钟）',
                                            `queue_position` INT DEFAULT 0 COMMENT '在当前设备队列中的位置',
                                            `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                            FOREIGN KEY (`patient_id`) REFERENCES `patients`(`patient_id`) ON DELETE CASCADE,
                                            FOREIGN KEY (`exam_id`) REFERENCES `medical_exams`(`exam_id`) ON DELETE CASCADE,
                                            FOREIGN KEY (`assigned_device_id`) REFERENCES `medical_devices`(`device_id`) ON DELETE SET NULL
) ENGINE=InnoDB;

-- 辅助表：检查项目前置约束表
CREATE TABLE `exam_prerequisites` (
                                      `exam_id` INT NOT NULL,
                                      `prerequisite_exam_id` INT NOT NULL COMMENT '必须先完成的检查项目ID',
                                      PRIMARY KEY (`exam_id`, `prerequisite_exam_id`),
                                      FOREIGN KEY (`exam_id`) REFERENCES `medical_exams`(`exam_id`) ON DELETE CASCADE,
                                      FOREIGN KEY (`prerequisite_exam_id`) REFERENCES `medical_exams`(`exam_id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 辅助表：设备实时状态表
CREATE TABLE `device_real_time_status` (
                                           `status_id` INT AUTO_INCREMENT PRIMARY KEY,
                                           `device_id` INT NOT NULL,
                                           `current_patient_id` INT COMMENT '当前正在检查的患者ID',
                                           `current_exam_id` INT COMMENT '当前正在进行的检查项目ID',
                                           `start_time` DATETIME COMMENT '当前检查开始时间',
                                           `expected_end_time` DATETIME COMMENT '预计结束时间',
                                           `status` ENUM('Idle', 'Busy', 'Maintenance', 'Offline') DEFAULT 'Idle',
                                           `queue_count` INT DEFAULT 0 COMMENT '当前排队人数',
                                           `utilization_rate` DECIMAL(5,2) DEFAULT 0.00 COMMENT '设备利用率（0-100%）',
                                           `last_updated` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                           FOREIGN KEY (`device_id`) REFERENCES `medical_devices`(`device_id`) ON DELETE CASCADE,
                                           FOREIGN KEY (`current_patient_id`) REFERENCES `patients`(`patient_id`) ON DELETE SET NULL,
                                           FOREIGN KEY (`current_exam_id`) REFERENCES `medical_exams`(`exam_id`) ON DELETE SET NULL
) ENGINE=InnoDB;