-- 使用数据库
USE TriageFlowSystem;

-- 插入测试检查项目数据
INSERT IGNORE INTO medical_exams (exam_id, exam_name, requires_fasting) VALUES
                                                                            (1, '胸部CT', FALSE),
                                                                            (2, 'B超', TRUE),
                                                                            (3, '抽血', TRUE),
                                                                            (4, '体重身高测量', FALSE),
                                                                            (5, '心电图', FALSE),
                                                                            (6, 'MRI', FALSE),
                                                                            (7, 'X光', FALSE),
                                                                            (8, '胃镜检查', TRUE),
                                                                            (9, '尿常规', FALSE),
                                                                            (10, '血压测量', FALSE);

-- 插入测试医疗设备数据
INSERT IGNORE INTO medical_devices (device_id, device_name, quantity, location) VALUES
                                                                                    (1, 'CT扫描仪', 2, '放射科A区'),
                                                                                    (2, 'B超机', 3, '超声科B区'),
                                                                                    (3, '抽血工作站', 4, '检验科C区'),
                                                                                    (4, '身高体重测量仪', 2, '体检中心D区'),
                                                                                    (5, '心电图机', 3, '心内科E区'),
                                                                                    (6, 'MRI机器', 1, '放射科F区'),
                                                                                    (7, 'X光机', 2, '放射科G区'),
                                                                                    (8, '胃镜设备', 2, '消化内科H区'),
                                                                                    (9, '尿液分析仪', 2, '检验科I区'),
                                                                                    (10, '血压计', 5, '各科室');

-- 插入设备检查能力数据
INSERT IGNORE INTO device_exam_capabilities (device_id, exam_id, duration_minutes) VALUES
-- CT扫描仪可以做的检查
(1, 1, 15),  -- 胸部CT
(1, 6, 30),  -- MRI
(1, 7, 10),  -- X光

-- B超机可以做的检查
(2, 2, 20),  -- B超
(2, 8, 25),  -- 胃镜检查

-- 抽血工作站可以做的检查
(3, 3, 5),   -- 抽血

-- 身高体重测量仪可以做的检查
(4, 4, 3),   -- 体重身高测量

-- 心电图机可以做的检查
(5, 5, 10),  -- 心电图

-- MRI机器可以做的检查
(6, 6, 30),  -- MRI

-- X光机可以做的检查
(7, 7, 10),  -- X光
(7, 1, 12),  -- 胸部CT

-- 胃镜设备可以做的检查
(8, 8, 25),  -- 胃镜检查

-- 尿液分析仪可以做的检查
(9, 9, 8),   -- 尿常规

-- 血压计可以做的检查
(10, 10, 2); -- 血压测量

-- 插入设备工作时间数据
INSERT IGNORE INTO device_working_schedules (device_id, day_of_week, start_time, end_time, is_working) VALUES
-- CT扫描仪工作时间 (周一至周五全天，周六上午)
(1, 1, '08:00:00', '17:00:00', TRUE),
(1, 2, '08:00:00', '17:00:00', TRUE),
(1, 3, '08:00:00', '17:00:00', TRUE),
(1, 4, '08:00:00', '17:00:00', TRUE),
(1, 5, '08:00:00', '17:00:00', TRUE),
(1, 6, '08:00:00', '12:00:00', TRUE),
(1, 7, '00:00:00', '23:59:59', FALSE),

-- B超机工作时间 (周一至周五全天)
(2, 1, '08:00:00', '17:00:00', TRUE),
(2, 2, '08:00:00', '17:00:00', TRUE),
(2, 3, '08:00:00', '17:00:00', TRUE),
(2, 4, '08:00:00', '17:00:00', TRUE),
(2, 5, '08:00:00', '17:00:00', TRUE),
(2, 6, '00:00:00', '23:59:59', FALSE),
(2, 7, '00:00:00', '23:59:59', FALSE),

-- 抽血工作站工作时间 (周一至周六全天)
(3, 1, '07:30:00', '16:30:00', TRUE),
(3, 2, '07:30:00', '16:30:00', TRUE),
(3, 3, '07:30:00', '16:30:00', TRUE),
(3, 4, '07:30:00', '16:30:00', TRUE),
(3, 5, '07:30:00', '16:30:00', TRUE),
(3, 6, '07:30:00', '12:00:00', TRUE),
(3, 7, '00:00:00', '23:59:59', FALSE),

-- 其他设备工作时间 (周一至周五全天)
(4, 1, '08:00:00', '17:00:00', TRUE),
(4, 2, '08:00:00', '17:00:00', TRUE),
(4, 3, '08:00:00', '17:00:00', TRUE),
(4, 4, '08:00:00', '17:00:00', TRUE),
(4, 5, '08:00:00', '17:00:00', TRUE),
(4, 6, '00:00:00', '23:59:59', FALSE),
(4, 7, '00:00:00', '23:59:59', FALSE),

(5, 1, '08:00:00', '17:00:00', TRUE),
(5, 2, '08:00:00', '17:00:00', TRUE),
(5, 3, '08:00:00', '17:00:00', TRUE),
(5, 4, '08:00:00', '17:00:00', TRUE),
(5, 5, '08:00:00', '17:00:00', TRUE),
(5, 6, '00:00:00', '23:59:59', FALSE),
(5, 7, '00:00:00', '23:59:59', FALSE);

-- 插入检查项目前置条件数据
INSERT IGNORE INTO exam_prerequisites (exam_id, prerequisite_exam_id) VALUES
                                                                          (8, 3),  -- 胃镜检查前需要先抽血
                                                                          (2, 8),  -- B超前需要先抽血
                                                                          (6, 1),  -- MRI前需要先做胸部CT
                                                                          (6, 5),  -- MRI前需要先做心电图
                                                                          (7, 1);  -- X光前需要先做胸部CT


-- 插入设备实时状态数据
INSERT IGNORE INTO device_real_time_status (device_id, status, queue_count, utilization_rate) VALUES
                                                                                                  (1, 'Idle', 0, 0),    -- CT扫描仪空闲
                                                                                                  (2, 'Idle', 2, 0),    -- B超机忙碌，有2人排队
                                                                                                  (3, 'Idle', 0, 0),    -- 抽血工作站空闲
                                                                                                  (4, 'Idle', 0, 0),    -- 身高体重测量仪空闲
                                                                                                  (5, 'Idle', 1, 0),    -- 心电图机忙碌，有1人排队
                                                                                                  (6, 'Idle', 0, 0),    -- MRI机器维护中
                                                                                                  (7, 'Idle', 0, 0),    -- X光机空闲
                                                                                                  (8, 'Idle', 0, 0),    -- 胃镜设备离线
                                                                                                  (9, 'Idle', 0, 0),    -- 尿液分析仪空闲
                                                                                                  (10, 'Idle', 0, 0);   -- 血压计空闲

-- 显示插入的数据统计
SELECT
    (SELECT COUNT(*) FROM medical_exams) AS exam_count,
    (SELECT COUNT(*) FROM medical_devices) AS device_count,
    (SELECT COUNT(*) FROM device_exam_capabilities) AS capability_count,
    (SELECT COUNT(*) FROM device_working_schedules) AS schedule_count,
    (SELECT COUNT(*) FROM exam_prerequisites) AS prerequisite_count,
    (SELECT COUNT(*) FROM device_real_time_status) AS status_count;