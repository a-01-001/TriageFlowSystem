package com.triageflow.entity;

import java.util.Date;

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

    public DeviceWorkingSchedule() {}

    public DeviceWorkingSchedule(int deviceId, int dayOfWeek, Date startTime, Date endTime, boolean isWorking) {
        this.deviceId = deviceId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isWorking = isWorking;
    }

    // Getter和Setter
    public int getScheduleId() { return scheduleId; }
    public void setScheduleId(int scheduleId) { this.scheduleId = scheduleId; }

    public int getDeviceId() { return deviceId; }
    public void setDeviceId(int deviceId) { this.deviceId = deviceId; }

    public int getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(int dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }

    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }

    public boolean isWorking() { return isWorking; }
    public void setWorking(boolean working) { isWorking = working; }

    public MedicalDevice getDevice() { return device; }
    public void setDevice(MedicalDevice device) { this.device = device; }

    // 辅助方法
    public boolean isWithinWorkingHours(Date currentTime) {
        // 实现工作时间检查逻辑
        return isWorking && currentTime.after(startTime) && currentTime.before(endTime);
    }

    @Override
    public String toString() {
        return "DeviceWorkingSchedule{" + "scheduleId=" + scheduleId + ", deviceId=" + deviceId +
                ", dayOfWeek=" + dayOfWeek + ", startTime=" + startTime + ", endTime=" + endTime +
                ", isWorking=" + isWorking + '}';
    }
}