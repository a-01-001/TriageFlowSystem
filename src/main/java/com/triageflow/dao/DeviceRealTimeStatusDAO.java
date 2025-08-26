package com.triageflow.dao;

import com.triageflow.entity.DeviceRealTimeStatus;
import java.util.List;
import java.util.Optional;

public interface DeviceRealTimeStatusDAO extends BaseDAO<DeviceRealTimeStatus> {
    void deleteByDeviceId(int deviceId);
    Optional<DeviceRealTimeStatus> findByDeviceId(int deviceId);
    List<DeviceRealTimeStatus> findByStatus(String status);

    // 核心状态管理方法
    boolean updateDeviceStatus(int deviceId, String status);
    boolean updateDeviceAssignment(int deviceId, Integer patientId, Integer examId);

    // 查询方法
    List<DeviceRealTimeStatus> findAvailableDevices();
    boolean isDeviceAvailable(int deviceId);

    // 扩展点：统计方法
    default int countByStatus(String status) { return 0; }
}