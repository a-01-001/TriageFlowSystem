package com.triageflow.dao;

import com.triageflow.entity.MedicalDevice;
import java.util.List;

public interface MedicalDeviceDAO {
    // 基本CRUD操作
    MedicalDevice getById(int deviceId);
    List<MedicalDevice> getAll();
    boolean insert(MedicalDevice device);
    boolean update(MedicalDevice device);
    boolean delete(int deviceId);

    // 特定查询方法
    List<MedicalDevice> getDevicesByExamId(int examId);
    List<MedicalDevice> getAvailableDevices();
    List<MedicalDevice> getDevicesByLocation(String location);
    List<MedicalDevice> getDevicesByName(String name);
    List<MedicalDevice> getDevicesByStatus(String status);
    int getDeviceCount();
    boolean updateDeviceQuantity(int deviceId, int newQuantity);
    boolean updateDeviceLocation(int deviceId, String newLocation);

    // 关联数据查询
    List<MedicalDevice> getDevicesWithCapabilities();
    List<MedicalDevice> getDevicesWithSchedules();
    List<MedicalDevice> getDevicesWithRealTimeStatus();
}