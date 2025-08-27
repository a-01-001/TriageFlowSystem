package com.triageflow.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
// 患者检查分配实体类
public class PatientExamAssignment {
    private int assignmentId;
    private int patientId;
    private int examId;
    private String status; // "Pending", "Scheduled", "In Progress", "Completed", "Cancelled"
    private int priority;
    private Date scheduledStartTime;
    private Date scheduledEndTime;
    private Date actualStartTime;
    private Date actualEndTime;
    private Integer assignedDeviceId;
    private int waitingTimeMinutes;
    private int queuePosition;
    private Date createdAt;

    // 关联数据
    private Patient patient;
    private MedicalExam exam;
    private MedicalDevice assignedDevice;


    // 辅助方法
    public boolean isCompleted() {
        return "Completed".equals(status);
    }

    public boolean isPending() {
        return "Pending".equals(status);
    }

    public boolean isScheduled() {
        return "Scheduled".equals(status) || "In Progress".equals(status);
    }

    public int getEstimatedDuration() {
        if (assignedDevice != null && exam != null) {
            // 从关联的设备能力对象中获取时长
            for (DeviceExamCapability capability : assignedDevice.getCapabilities()) {
                if (capability.getExamId() == exam.getExamId()) {
                    return capability.getDurationMinutes();
                }
            }
        }
        // 如果没有分配设备或找不到对应能力，返回默认值
        return 0;
    }
}