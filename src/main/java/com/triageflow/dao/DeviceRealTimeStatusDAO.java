package com.triageflow.dao;

import com.triageflow.entity.DeviceRealTimeStatus;
import java.util.Date;
import java.util.List;

public interface DeviceRealTimeStatusDAO {
    // 基本CRUD操作
    DeviceRealTimeStatus getById(int statusId);
    DeviceRealTimeStatus getByDeviceId(int deviceId);
    List<DeviceRealTimeStatus> getAll();
    boolean insert(DeviceRealTimeStatus status);
    boolean update(DeviceRealTimeStatus status);
    boolean delete(int statusId);

    // 特定查询方法
    List<DeviceRealTimeStatus> getByStatus(String status);
    List<DeviceRealTimeStatus> getAvailableDevices();
    List<DeviceRealTimeStatus> getBusyDevices();
    List<DeviceRealTimeStatus> getMaintenanceDevices();
    List<DeviceRealTimeStatus> getOfflineDevices();

    // 更新方法
    boolean updateStatus(int deviceId, String newStatus);
    boolean updateCurrentPatient(int deviceId, Integer patientId);
    boolean updateCurrentExam(int deviceId, Integer examId);
    boolean updateQueueCount(int deviceId, int newCount);
    boolean incrementQueueCount(int deviceId);
    boolean decrementQueueCount(int deviceId);
    boolean updateUtilizationRate(int deviceId, double newRate);
    boolean updateStartTime(int deviceId, Date newStartTime);
    boolean updateExpectedEndTime(int deviceId, Date newEndTime);

    // 检查方法
    boolean isDeviceAvailable(int deviceId);
    boolean isDeviceBusy(int deviceId);

    // 统计方法
    int getQueueCount(int deviceId);
    double getUtilizationRate(int deviceId);
    int getBusyDeviceCount();
    int getAvailableDeviceCount();

    // 关联数据查询
    List<DeviceRealTimeStatus> getAllWithDevice();
    List<DeviceRealTimeStatus> getAllWithCurrentPatient();
    List<DeviceRealTimeStatus> getAllWithCurrentExam();
}