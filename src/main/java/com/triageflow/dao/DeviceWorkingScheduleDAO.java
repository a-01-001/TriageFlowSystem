package com.triageflow.dao;

import com.triageflow.entity.DeviceWorkingSchedule;
import java.util.Date;
import java.util.List;

public interface DeviceWorkingScheduleDAO {
    // 基本CRUD操作
    DeviceWorkingSchedule getById(int scheduleId);
    List<DeviceWorkingSchedule> getAll();
    boolean insert(DeviceWorkingSchedule schedule);
    boolean update(DeviceWorkingSchedule schedule);
    boolean delete(int scheduleId);

    // 特定查询方法
    List<DeviceWorkingSchedule> getByDeviceId(int deviceId);
    List<DeviceWorkingSchedule> getByDayOfWeek(int dayOfWeek);
    List<DeviceWorkingSchedule> getWorkingSchedules();
    List<DeviceWorkingSchedule> getNonWorkingSchedules();
    List<DeviceWorkingSchedule> getSchedulesByTimeRange(Date startTime, Date endTime);

    // 检查方法
    boolean isDeviceWorking(int deviceId, int dayOfWeek, Date currentTime);
    boolean hasWorkingSchedule(int deviceId, int dayOfWeek);

    // 更新方法
    boolean updateWorkingStatus(int scheduleId, boolean isWorking);
    boolean updateTimeRange(int scheduleId, Date newStartTime, Date newEndTime);

    // 统计方法
    int getScheduleCountForDevice(int deviceId);
    int getWorkingDayCountForDevice(int deviceId);

    // 关联数据查询
    List<DeviceWorkingSchedule> getAllWithDevice();
}