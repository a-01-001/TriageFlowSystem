package com.triageflow.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
// 医疗设备实体类
public class MedicalDevice {
    private int deviceId;
    private String deviceName;
    private int quantity;
    private String location;
    private Date createdAt;

    // 关联数据
    private List<DeviceExamCapability> capabilities;
    private List<DeviceWorkingSchedule> workingSchedules;
    private DeviceRealTimeStatus realTimeStatus;

    // 辅助方法
    public boolean isAvailable() {
        return realTimeStatus != null && "Idle".equals(realTimeStatus.getStatus());
    }

    public int getCurrentQueueCount() {
        return realTimeStatus != null ? realTimeStatus.getQueueCount() : 0;
    }
}
