package com.triageflow.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
// 设备工作时间安排实体类
public class DeviceWorkingSchedule {
    private int scheduleId;
    private int deviceId;
    private int dayOfWeek; // 1-7: Monday-Sunday
    private Date startTime;
    private Date endTime;
    private boolean isWorking;

    // 关联数据
    private MedicalDevice device;

    // 辅助方法
    public boolean isWithinWorkingHours(Date currentTime) {
        // 实现工作时间检查逻辑
        return isWorking && currentTime.after(startTime) && currentTime.before(endTime);
    }
}