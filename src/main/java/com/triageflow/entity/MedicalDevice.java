package com.triageflow.entity;

import java.util.Date;
import java.util.List;

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

    public MedicalDevice() {}

    public MedicalDevice(String deviceName, int quantity, String location) {
        this.deviceName = deviceName;
        this.quantity = quantity;
        this.location = location;
    }

    // Getter和Setter
    public int getDeviceId() { return deviceId; }
    public void setDeviceId(int deviceId) { this.deviceId = deviceId; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public List<DeviceExamCapability> getCapabilities() { return capabilities; }
    public void setCapabilities(List<DeviceExamCapability> capabilities) { this.capabilities = capabilities; }

    public List<DeviceWorkingSchedule> getWorkingSchedules() { return workingSchedules; }
    public void setWorkingSchedules(List<DeviceWorkingSchedule> workingSchedules) { this.workingSchedules = workingSchedules; }

    public DeviceRealTimeStatus getRealTimeStatus() { return realTimeStatus; }
    public void setRealTimeStatus(DeviceRealTimeStatus realTimeStatus) { this.realTimeStatus = realTimeStatus; }

    // 辅助方法
    public boolean isAvailable() {
        return realTimeStatus != null && "Idle".equals(realTimeStatus.getStatus());
    }

    public int getCurrentQueueCount() {
        return realTimeStatus != null ? realTimeStatus.getQueueCount() : 0;
    }

    @Override
    public String toString() {
        return "MedicalDevice{" + "deviceId=" + deviceId + ", deviceName='" + deviceName + '\'' +
                ", quantity=" + quantity + ", location='" + location + '\'' + '}';
    }
}
