package com.triageflow.dao.impl;

import com.triageflow.dao.DeviceExamCapabilityDAO;
import com.triageflow.entity.DeviceExamCapability;
import com.triageflow.utils.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DeviceExamCapabilityDAOImpl implements DeviceExamCapabilityDAO {

    private static final Logger logger = LoggerFactory.getLogger(DeviceExamCapabilityDAOImpl.class);

    @Override
    public Optional<DeviceExamCapability> findById(int id) {
        // 注意：DeviceExamCapability没有单独的ID字段，使用复合主键
        // 这里我们假设有一个隐藏的ID字段，但实际上应该使用findByDeviceAndExam方法
        return Optional.empty();
    }

    @Override
    public List<DeviceExamCapability> findAll() {
        List<DeviceExamCapability> capabilities = new ArrayList<>();
        String sql = "SELECT * FROM device_exam_capabilities";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                capabilities.add(mapResultSetToDeviceExamCapability(rs));
            }
        } catch (SQLException e) {
            logger.error("查询所有设备检查能力记录失败", e);
        }
        return capabilities;
    }

    @Override
    public DeviceExamCapability save(DeviceExamCapability capability) {
        String sql = "INSERT INTO device_exam_capabilities (device_id, exam_id, duration_minutes) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, capability.getDeviceId());
            stmt.setInt(2, capability.getExamId());
            stmt.setInt(3, capability.getDurationMinutes());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("保存设备检查能力失败", e);
        }
        return capability;
    }

    @Override
    public DeviceExamCapability update(DeviceExamCapability capability) {
        String sql = "UPDATE device_exam_capabilities SET duration_minutes = ? WHERE device_id = ? AND exam_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, capability.getDurationMinutes());
            stmt.setInt(2, capability.getDeviceId());
            stmt.setInt(3, capability.getExamId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("更新设备检查能力失败", e);
        }
        return capability;
    }

    @Override
    public void delete(int id) {
        // 注意：DeviceExamCapability没有单独的ID字段，使用复合主键
        // 这里我们假设有一个隐藏的ID字段，但实际上应该使用deleteByDeviceAndExam方法
    }

    @Override
    public void deleteByDeviceAndExam(int deviceId, int examId) {
        String sql = "DELETE FROM device_exam_capabilities WHERE device_id = ? AND exam_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, deviceId);
            stmt.setInt(2, examId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("删除设备检查能力失败", e);
        }
    }

    @Override
    public Optional<DeviceExamCapability> findByDeviceAndExam(int deviceId, int examId) {
        String sql = "SELECT * FROM device_exam_capabilities WHERE device_id = ? AND exam_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, deviceId);
            stmt.setInt(2, examId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToDeviceExamCapability(rs));
            }
        } catch (SQLException e) {
            logger.error("查找设备检查能力失败", e);
        }
        return Optional.empty();
    }

    @Override
    public List<DeviceExamCapability> findByDeviceId(int deviceId) {
        List<DeviceExamCapability> capabilities = new ArrayList<>();
        String sql = "SELECT * FROM device_exam_capabilities WHERE device_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, deviceId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                capabilities.add(mapResultSetToDeviceExamCapability(rs));
            }
        } catch (SQLException e) {
            logger.error("根据设备ID查找检查能力失败", e);
        }
        return capabilities;
    }

    @Override
    public List<DeviceExamCapability> findByExamId(int examId) {
        List<DeviceExamCapability> capabilities = new ArrayList<>();
        String sql = "SELECT * FROM device_exam_capabilities WHERE exam_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, examId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                capabilities.add(mapResultSetToDeviceExamCapability(rs));
            }
        } catch (SQLException e) {
            logger.error("根据检查ID查找设备能力失败", e);
        }
        return capabilities;
    }

    @Override
    public int getExamDuration(int deviceId, int examId) {
        String sql = "SELECT duration_minutes FROM device_exam_capabilities WHERE device_id = ? AND exam_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, deviceId);
            stmt.setInt(2, examId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("duration_minutes");
            }
        } catch (SQLException e) {
            logger.error("获取检查持续时间失败", e);
        }
        return 0;
    }

    @Override
    public boolean canPerformExam(int deviceId, int examId) {
        String sql = "SELECT COUNT(*) FROM device_exam_capabilities WHERE device_id = ? AND exam_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, deviceId);
            stmt.setInt(2, examId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.error("检查设备能否执行检查失败", e);
        }
        return false;
    }

    private DeviceExamCapability mapResultSetToDeviceExamCapability(ResultSet rs) throws SQLException {
        DeviceExamCapability capability = new DeviceExamCapability();
        capability.setDeviceId(rs.getInt("device_id"));
        capability.setExamId(rs.getInt("exam_id"));
        capability.setDurationMinutes(rs.getInt("duration_minutes"));
        return capability;
    }
}