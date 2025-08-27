package com.triageflow.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
// 设备实时状态实体类
public class DeviceRealTimeStatus {
    private int statusId;
    private int deviceId;
    private Integer currentPatientId;
    private Integer currentExamId;
    private Date startTime;
    private Date expectedEndTime;
    private String status; // "Idle", "Busy", "Maintenance", "Offline"
    private int queueCount;
    private double utilizationRate;
    private Date lastUpdated;

    // 关联数据
    private MedicalDevice device;
    private Patient currentPatient;
    private MedicalExam currentExam;

    // 辅助方法
    public boolean isAvailable() {
        return "Idle".equals(status);
    }

    public boolean isBusy() {
        return "Busy".equals(status);
    }

    public int getRemainingTime() {
        if (expectedEndTime != null && startTime != null) {
            long remaining = expectedEndTime.getTime() - System.currentTimeMillis();
            return (int) (remaining / (1000 * 60)); // 返回剩余分钟数
        }
        return 0;
    }
}
