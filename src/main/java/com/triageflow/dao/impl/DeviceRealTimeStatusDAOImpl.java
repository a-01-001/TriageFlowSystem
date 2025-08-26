package com.triageflow.dao.impl;

import com.triageflow.dao.DeviceRealTimeStatusDAO;
import com.triageflow.entity.DeviceRealTimeStatus;
import com.triageflow.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DeviceRealTimeStatusDAOImpl implements DeviceRealTimeStatusDAO {

    @Override
    public Optional<DeviceRealTimeStatus> findById(int id) {
        String sql = "SELECT * FROM device_real_time_status WHERE status_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToDeviceRealTimeStatus(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<DeviceRealTimeStatus> findAll() {
        List<DeviceRealTimeStatus> statuses = new ArrayList<>();
        String sql = "SELECT * FROM device_real_time_status";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                statuses.add(mapResultSetToDeviceRealTimeStatus(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statuses;
    }

    @Override
    public DeviceRealTimeStatus save(DeviceRealTimeStatus status) {
        String sql = "INSERT INTO device_real_time_status (device_id, current_patient_id, current_exam_id, " +
                "start_time, expected_end_time, status, queue_count, utilization_rate) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, status.getDeviceId());
            setNullableInt(stmt, 2, status.getCurrentPatientId());
            setNullableInt(stmt, 3, status.getCurrentExamId());
            setNullableTimestamp(stmt, 4, status.getStartTime());
            setNullableTimestamp(stmt, 5, status.getExpectedEndTime());
            stmt.setString(6, status.getStatus());
            stmt.setInt(7, status.getQueueCount());
            stmt.setDouble(8, status.getUtilizationRate());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                status.setStatusId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return status;
    }

    @Override
    public DeviceRealTimeStatus update(DeviceRealTimeStatus status) {
        String sql = "UPDATE device_real_time_status SET device_id = ?, current_patient_id = ?, current_exam_id = ?, " +
                "start_time = ?, expected_end_time = ?, status = ?, queue_count = ?, utilization_rate = ? " +
                "WHERE status_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, status.getDeviceId());
            setNullableInt(stmt, 2, status.getCurrentPatientId());
            setNullableInt(stmt, 3, status.getCurrentExamId());
            setNullableTimestamp(stmt, 4, status.getStartTime());
            setNullableTimestamp(stmt, 5, status.getExpectedEndTime());
            stmt.setString(6, status.getStatus());
            stmt.setInt(7, status.getQueueCount());
            stmt.setDouble(8, status.getUtilizationRate());
            stmt.setInt(9, status.getStatusId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return status;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM device_real_time_status WHERE status_id = ?";
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
        String sql = "DELETE FROM device_real_time_status WHERE device_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, deviceId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<DeviceRealTimeStatus> findByDeviceId(int deviceId) {
        String sql = "SELECT * FROM device_real_time_status WHERE device_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, deviceId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToDeviceRealTimeStatus(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<DeviceRealTimeStatus> findByStatus(String status) {
        List<DeviceRealTimeStatus> statuses = new ArrayList<>();
        String sql = "SELECT * FROM device_real_time_status WHERE status = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                statuses.add(mapResultSetToDeviceRealTimeStatus(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statuses;
    }

    @Override
    public boolean updateDeviceStatus(int deviceId, String status) {
        String sql = "UPDATE device_real_time_status SET status = ? WHERE device_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, deviceId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateDeviceAssignment(int deviceId, Integer patientId, Integer examId) {
        String sql = "UPDATE device_real_time_status SET current_patient_id = ?, current_exam_id = ? WHERE device_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setNullableInt(stmt, 1, patientId);
            setNullableInt(stmt, 2, examId);
            stmt.setInt(3, deviceId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<DeviceRealTimeStatus> findAvailableDevices() {
        List<DeviceRealTimeStatus> statuses = new ArrayList<>();
        String sql = "SELECT * FROM device_real_time_status WHERE status = 'Idle'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                statuses.add(mapResultSetToDeviceRealTimeStatus(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statuses;
    }

    @Override
    public boolean isDeviceAvailable(int deviceId) {
        String sql = "SELECT status FROM device_real_time_status WHERE device_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, deviceId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return "Idle".equals(rs.getString("status"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM device_real_time_status WHERE status = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private DeviceRealTimeStatus mapResultSetToDeviceRealTimeStatus(ResultSet rs) throws SQLException {
        DeviceRealTimeStatus status = new DeviceRealTimeStatus();
        status.setStatusId(rs.getInt("status_id"));
        status.setDeviceId(rs.getInt("device_id"));
        status.setCurrentPatientId(rs.getInt("current_patient_id"));
        if (rs.wasNull()) status.setCurrentPatientId(null);
        status.setCurrentExamId(rs.getInt("current_exam_id"));
        if (rs.wasNull()) status.setCurrentExamId(null);
        status.setStartTime(rs.getTimestamp("start_time"));
        status.setExpectedEndTime(rs.getTimestamp("expected_end_time"));
        status.setStatus(rs.getString("status"));
        status.setQueueCount(rs.getInt("queue_count"));
        status.setUtilizationRate(rs.getDouble("utilization_rate"));
        status.setLastUpdated(rs.getTimestamp("last_updated"));
        return status;
    }

    private void setNullableInt(PreparedStatement stmt, int index, Integer value) throws SQLException {
        if (value != null) {
            stmt.setInt(index, value);
        } else {
            stmt.setNull(index, Types.INTEGER);
        }
    }

    private void setNullableTimestamp(PreparedStatement stmt, int index, java.util.Date value) throws SQLException {
        if (value != null) {
            stmt.setTimestamp(index, new Timestamp(value.getTime()));
        } else {
            stmt.setNull(index, Types.TIMESTAMP);
        }
    }
}