package com.triageflow.entity;

// 设备检查能力实体类
public class DeviceExamCapability {
    private int deviceId;
    private int examId;
    private int durationMinutes;

    // 关联数据
    private MedicalDevice device;
    private MedicalExam exam;

    public DeviceExamCapability() {}

    public DeviceExamCapability(int deviceId, int examId, int durationMinutes) {
        this.deviceId = deviceId;
        this.examId = examId;
        this.durationMinutes = durationMinutes;
    }

    // Getter和Setter
    public int getDeviceId() { return deviceId; }
    public void setDeviceId(int deviceId) { this.deviceId = deviceId; }

    public int getExamId() { return examId; }
    public void setExamId(int examId) { this.examId = examId; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public MedicalDevice getDevice() { return device; }
    public void setDevice(MedicalDevice device) { this.device = device; }

    public MedicalExam getExam() { return exam; }
    public void setExam(MedicalExam exam) { this.exam = exam; }

    @Override
    public String toString() {
        return "DeviceExamCapability{" + "deviceId=" + deviceId + ", examId=" + examId +
                ", durationMinutes=" + durationMinutes + '}';
    }
}