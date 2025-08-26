package com.triageflow.dao.impl;

import com.triageflow.dao.DeviceWorkingScheduleDAO;
import com.triageflow.entity.DeviceWorkingSchedule;
import com.triageflow.utils.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.Date;

public class DeviceWorkingScheduleDAOImpl implements DeviceWorkingScheduleDAO {

    private static final Logger logger = LoggerFactory.getLogger(DeviceWorkingScheduleDAOImpl.class);

    @Override
    public Optional<DeviceWorkingSchedule> findById(int id) {
        String sql = "SELECT * FROM device_working_schedules WHERE schedule_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToDeviceWorkingSchedule(rs));
            }
        } catch (SQLException e) {
            logger.error("根据ID查询设备工作计划失败", e);
        }
        return Optional.empty();
    }

    @Override
    public List<DeviceWorkingSchedule> findAll() {
        List<DeviceWorkingSchedule> schedules = new ArrayList<>();
        String sql = "SELECT * FROM device_working_schedules";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                schedules.add(mapResultSetToDeviceWorkingSchedule(rs));
            }
        } catch (SQLException e) {
            logger.error("查询所有设备工作计划失败", e);
        }
        return schedules;
    }

    @Override
    public DeviceWorkingSchedule save(DeviceWorkingSchedule schedule) {
        String sql = "INSERT INTO device_working_schedules (device_id, day_of_week, start_time, end_time, is_working) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, schedule.getDeviceId());
            stmt.setInt(2, schedule.getDayOfWeek());
            stmt.setTime(3, new Time(schedule.getStartTime().getTime()));
            stmt.setTime(4, new Time(schedule.getEndTime().getTime()));
            stmt.setBoolean(5, schedule.isWorking());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                schedule.setScheduleId(rs.getInt(1));
            }
        } catch (SQLException e) {
            logger.error("保存设备工作计划失败", e);
        }
        return schedule;
    }

    @Override
    public DeviceWorkingSchedule update(DeviceWorkingSchedule schedule) {
        String sql = "UPDATE device_working_schedules SET device_id = ?, day_of_week = ?, start_time = ?, end_time = ?, is_working = ? WHERE schedule_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, schedule.getDeviceId());
            stmt.setInt(2, schedule.getDayOfWeek());
            stmt.setTime(3, new Time(schedule.getStartTime().getTime()));
            stmt.setTime(4, new Time(schedule.getEndTime().getTime()));
            stmt.setBoolean(5, schedule.isWorking());
            stmt.setInt(6, schedule.getScheduleId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("更新设备工作计划失败", e);
        }
        return schedule;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM device_working_schedules WHERE schedule_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("删除设备工作计划失败", e);
        }
    }

    @Override
    public void deleteByDeviceId(int deviceId) {
        String sql = "DELETE FROM device_working_schedules WHERE device_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, deviceId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("根据设备ID删除工作计划失败", e);
        }
    }

    @Override
    public List<DeviceWorkingSchedule> findByDeviceId(int deviceId) {
        List<DeviceWorkingSchedule> schedules = new ArrayList<>();
        String sql = "SELECT * FROM device_working_schedules WHERE device_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, deviceId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                schedules.add(mapResultSetToDeviceWorkingSchedule(rs));
            }
        } catch (SQLException e) {
            logger.error("根据设备ID查询工作计划失败", e);
        }
        return schedules;
    }

    @Override
    public boolean isDeviceWorking(int deviceId, Date checkTime) {
        // 转换为LocalDateTime以便更容易处理
        LocalDateTime localCheckTime = checkTime.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        // 获取星期几 (1=周一, 7=周日)
        DayOfWeek dayOfWeek = localCheckTime.getDayOfWeek();
        int adjustedDayOfWeek = dayOfWeek.getValue();

        // 获取当前时间
        LocalTime localTime = localCheckTime.toLocalTime();

        String sql = "SELECT * FROM device_working_schedules WHERE device_id = ? AND day_of_week = ? AND is_working = TRUE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, deviceId);
            stmt.setInt(2, adjustedDayOfWeek);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LocalTime startTime = rs.getTime("start_time").toLocalTime();
                LocalTime endTime = rs.getTime("end_time").toLocalTime();

                if (!localTime.isBefore(startTime) && !localTime.isAfter(endTime)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            logger.error("检查设备工作状态失败", e);
        }

        logger.debug("未找到设备{}在星期{}时间{}的有效工作计划", deviceId, adjustedDayOfWeek, localTime);
        return false;
    }

    @Override
    public List<DeviceWorkingSchedule> findWorkingSchedules() {
        List<DeviceWorkingSchedule> schedules = new ArrayList<>();
        String sql = "SELECT * FROM device_working_schedules WHERE is_working = TRUE";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                schedules.add(mapResultSetToDeviceWorkingSchedule(rs));
            }
        } catch (SQLException e) {
            logger.error("查询所有启用的工作计划失败", e);
        }
        return schedules;
    }

    private DeviceWorkingSchedule mapResultSetToDeviceWorkingSchedule(ResultSet rs) throws SQLException {
        DeviceWorkingSchedule schedule = new DeviceWorkingSchedule();
        schedule.setScheduleId(rs.getInt("schedule_id"));
        schedule.setDeviceId(rs.getInt("device_id"));
        schedule.setDayOfWeek(rs.getInt("day_of_week"));
        schedule.setStartTime(rs.getTime("start_time"));
        schedule.setEndTime(rs.getTime("end_time"));
        schedule.setWorking(rs.getBoolean("is_working"));
        return schedule;
    }
}