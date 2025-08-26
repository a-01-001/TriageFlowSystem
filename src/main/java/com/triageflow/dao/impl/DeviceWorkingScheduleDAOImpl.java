package com.triageflow.dao.impl;

import com.triageflow.dao.DeviceWorkingScheduleDAO;
import com.triageflow.entity.DeviceWorkingSchedule;
import com.triageflow.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class DeviceWorkingScheduleDAOImpl implements DeviceWorkingScheduleDAO {

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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return schedules;
    }

    @Override
    public boolean isDeviceWorking(int deviceId, Date checkTime) {
        // 获取当前日期的星期几
        Calendar cal = Calendar.getInstance();

        cal.setTime(checkTime);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        // 调整星期几的表示方式 (Calendar中周日=1, 周一=2, 我们需要周一=1, 周日=7)
        int adjustedDayOfWeek = dayOfWeek - 1;
        if (adjustedDayOfWeek == 0) adjustedDayOfWeek = 7;

        // 转换为小时:分钟:秒格式用于比较
        cal.setTime(checkTime);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);

        int currentTimeInSeconds = hour * 3600 + minute * 60 + second;

        // 获取当前时间
        // Time currentTime = new Time(checkTime.getTime());

        String sql = "SELECT * FROM device_working_schedules WHERE device_id = ? AND day_of_week = ? AND is_working = TRUE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, deviceId);
            stmt.setInt(2, adjustedDayOfWeek);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Time startTime = rs.getTime("start_time");
                Time endTime = rs.getTime("end_time");

                // 提取开始时间和结束时间的小时、分钟和秒
                Calendar startCal = Calendar.getInstance();
                startCal.setTime(startTime);
                int startHour = startCal.get(Calendar.HOUR_OF_DAY);
                int startMinute = startCal.get(Calendar.MINUTE);
                int startSecond = startCal.get(Calendar.SECOND);

                Calendar endCal = Calendar.getInstance();
                endCal.setTime(endTime);
                int endHour = endCal.get(Calendar.HOUR_OF_DAY);
                int endMinute = endCal.get(Calendar.MINUTE);
                int endSecond = endCal.get(Calendar.SECOND);

                // 转换为秒数进行比较
                int startTimeInSeconds = startHour * 3600 + startMinute * 60 + startSecond;
                int endTimeInSeconds = endHour * 3600 + endMinute * 60 + endSecond;

                //System.out.println("currentTimeInSeconds: " + currentTimeInSeconds);
                //System.out.println("startTimeInSeconds: " + startTimeInSeconds);
                //System.out.println("endTimeInSeconds: " + endTimeInSeconds);

                // 检查当前时间是否在工作时间段内 (包括结束时间)
                if (currentTimeInSeconds >= startTimeInSeconds && currentTimeInSeconds <= endTimeInSeconds) {
                    return true;
                }

/*----------------------屎
                System.out.println("currenTime: " + currentTime);

                System.out.println("startTime: " + startTime);
                System.out.println("endTime: " + endTime);

                System.out.println("after: " + currentTime.after(startTime));
                System.out.println("before: " + currentTime.before(endTime));

                        currenTime: 16:31:40
                        startTime: 15:31:40
                        endTime: 17:31:40
                        after: true
                        before: false

                // 检查当前时间是否在工作时间段内
                if (currentTime.after(startTime) && currentTime.before(endTime)) {
                    return true;
                }
                */
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("No working schedule found for device " + deviceId + " on day " + adjustedDayOfWeek + " at time " + checkTime);
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
            e.printStackTrace();
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