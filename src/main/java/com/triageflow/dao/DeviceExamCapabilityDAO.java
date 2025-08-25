package com.triageflow.dao;

import com.triageflow.entity.DeviceExamCapability;
import java.util.List;

public interface DeviceExamCapabilityDAO {
    // 基本CRUD操作
    DeviceExamCapability getByDeviceAndExam(int deviceId, int examId);
    List<DeviceExamCapability> getAll();
    boolean insert(DeviceExamCapability capability);
    boolean update(DeviceExamCapability capability);
    boolean delete(int deviceId, int examId);

    // 特定查询方法
    List<DeviceExamCapability> getByDeviceId(int deviceId);
    List<DeviceExamCapability> getByExamId(int examId);
    int getDuration(int deviceId, int examId);
    boolean updateDuration(int deviceId, int examId, int newDuration);

    // 检查方法
    boolean deviceCanPerformExam(int deviceId, int examId);
    boolean examCanBePerformedByDevice(int examId, int deviceId);

    // 统计方法
    int getDeviceCapabilityCount(int deviceId);
    int getExamCapabilityCount(int examId);

    // 关联数据查询
    List<DeviceExamCapability> getCapabilitiesWithDevice();
    List<DeviceExamCapability> getCapabilitiesWithExam();
    List<DeviceExamCapability> getAllWithAssociations();
}