package com.triageflow.entity;

import java.util.Date;

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

    public DeviceRealTimeStatus() {}

    public DeviceRealTimeStatus(int deviceId, String status) {
        this.deviceId = deviceId;
        this.status = status;
    }

    // Getter和Setter
    public int getStatusId() { return statusId; }
    public void setStatusId(int statusId) { this.statusId = statusId; }

    public int getDeviceId() { return deviceId; }
    public void setDeviceId(int deviceId) { this.deviceId = deviceId; }

    public Integer getCurrentPatientId() { return currentPatientId; }
    public void setCurrentPatientId(Integer currentPatientId) { this.currentPatientId = currentPatientId; }

    public Integer getCurrentExamId() { return currentExamId; }
    public void setCurrentExamId(Integer currentExamId) { this.currentExamId = currentExamId; }

    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }

    public Date getExpectedEndTime() { return expectedEndTime; }
    public void setExpectedEndTime(Date expectedEndTime) { this.expectedEndTime = expectedEndTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getQueueCount() { return queueCount; }
    public void setQueueCount(int queueCount) { this.queueCount = queueCount; }

    public double getUtilizationRate() { return utilizationRate; }
    public void setUtilizationRate(double utilizationRate) { this.utilizationRate = utilizationRate; }

    public Date getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Date lastUpdated) { this.lastUpdated = lastUpdated; }

    public MedicalDevice getDevice() { return device; }
    public void setDevice(MedicalDevice device) { this.device = device; }

    public Patient getCurrentPatient() { return currentPatient; }
    public void setCurrentPatient(Patient currentPatient) { this.currentPatient = currentPatient; }

    public MedicalExam getCurrentExam() { return currentExam; }
    public void setCurrentExam(MedicalExam currentExam) { this.currentExam = currentExam; }

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

    @Override
    public String toString() {
        return "DeviceRealTimeStatus{" + "statusId=" + statusId + ", deviceId=" + deviceId +
                ", status='" + status + '\'' + ", queueCount=" + queueCount +
                ", utilizationRate=" + utilizationRate + '}';
    }
}
