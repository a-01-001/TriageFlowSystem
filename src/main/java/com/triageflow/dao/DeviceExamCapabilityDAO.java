package com.triageflow.dao;

import com.triageflow.entity.DeviceExamCapability;
import java.util.List;
import java.util.Optional;

public interface DeviceExamCapabilityDAO extends BaseDAO<DeviceExamCapability> {
    void deleteByDeviceAndExam(int deviceId, int examId);
    Optional<DeviceExamCapability> findByDeviceAndExam(int deviceId, int examId);
    List<DeviceExamCapability> findByDeviceId(int deviceId);
    List<DeviceExamCapability> findByExamId(int examId);

    // 业务核心方法
    int getExamDuration(int deviceId, int examId);
    boolean canPerformExam(int deviceId, int examId);
}