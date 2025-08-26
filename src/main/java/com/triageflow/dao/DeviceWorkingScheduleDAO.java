package com.triageflow.dao;

import com.triageflow.entity.DeviceWorkingSchedule;
import java.util.Date;
import java.util.List;

public interface DeviceWorkingScheduleDAO extends BaseDAO<DeviceWorkingSchedule> {
    void deleteByDeviceId(int deviceId);
    List<DeviceWorkingSchedule> findByDeviceId(int deviceId);

    // 核心业务方法
    boolean isDeviceWorking(int deviceId, Date checkTime);

    // 扩展点
    default List<DeviceWorkingSchedule> findWorkingSchedules() { return List.of(); }
}