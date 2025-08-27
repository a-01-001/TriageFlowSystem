package com.triageflow.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// 设备检查能力实体类
public class DeviceExamCapability {
    private int deviceId;
    private int examId;
    private int durationMinutes;

    // 关联数据
    private MedicalDevice device;
    private MedicalExam exam;
}