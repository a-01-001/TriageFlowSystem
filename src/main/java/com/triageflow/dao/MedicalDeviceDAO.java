package com.triageflow.dao;

import com.triageflow.entity.MedicalDevice;
import java.util.List;
import java.util.Optional;

public interface MedicalDeviceDAO extends BaseDAO<MedicalDevice> {
    // 核心查询方法
    List<MedicalDevice> findByLocation(String location);
    Optional<MedicalDevice> findByName(String name);
    List<MedicalDevice> findAvailableDevices();

    // 扩展点
    default List<MedicalDevice> findDevicesByExam(int examId) { return List.of(); }
    default int countDevices() { return 0; }
}