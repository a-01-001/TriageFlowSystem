package com.triageflow.dao.impl;

import com.triageflow.dao.MedicalDeviceDAO;
import com.triageflow.entity.MedicalDevice;
import com.triageflow.utils.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MedicalDeviceDAOImpl implements MedicalDeviceDAO {

    private static final Logger logger = LoggerFactory.getLogger(MedicalDeviceDAOImpl.class);

    @Override
    public Optional<MedicalDevice> findById(int id) {
        String sql = "SELECT * FROM medical_devices WHERE device_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToMedicalDevice(rs));
            }
        } catch (SQLException e) {
            logger.error("根据ID查询医疗设备失败", e);
        }
        return Optional.empty();
    }

    @Override
    public List<MedicalDevice> findAll() {
        List<MedicalDevice> devices = new ArrayList<>();
        String sql = "SELECT * FROM medical_devices";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                devices.add(mapResultSetToMedicalDevice(rs));
            }
        } catch (SQLException e) {
            logger.error("查询所有医疗设备失败", e);
        }
        return devices;
    }

    @Override
    public MedicalDevice save(MedicalDevice device) {
        String sql = "INSERT INTO medical_devices (device_name, quantity, location) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, device.getDeviceName());
            stmt.setInt(2, device.getQuantity());
            stmt.setString(3, device.getLocation());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                device.setDeviceId(rs.getInt(1));
            }
        } catch (SQLException e) {
            logger.error("保存医疗设备失败", e);
        }
        return device;
    }

    @Override
    public MedicalDevice update(MedicalDevice device) {
        String sql = "UPDATE medical_devices SET device_name = ?, quantity = ?, location = ? WHERE device_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, device.getDeviceName());
            stmt.setInt(2, device.getQuantity());
            stmt.setString(3, device.getLocation());
            stmt.setInt(4, device.getDeviceId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("更新医疗设备失败", e);
        }
        return device;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM medical_devices WHERE device_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("删除医疗设备失败", e);
        }
    }

    @Override
    public List<MedicalDevice> findByLocation(String location) {
        List<MedicalDevice> devices = new ArrayList<>();
        String sql = "SELECT * FROM medical_devices WHERE location LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + location + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                devices.add(mapResultSetToMedicalDevice(rs));
            }
        } catch (SQLException e) {
            logger.error("根据位置查询医疗设备失败", e);
        }
        return devices;
    }

    @Override
    public Optional<MedicalDevice> findByName(String name) {
        String sql = "SELECT * FROM medical_devices WHERE device_name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToMedicalDevice(rs));
            }
        } catch (SQLException e) {
            logger.error("根据名称查询医疗设备失败", e);
        }
        return Optional.empty();
    }

    @Override
    public List<MedicalDevice> findAvailableDevices() {
        List<MedicalDevice> devices = new ArrayList<>();
        String sql = "SELECT md.* FROM medical_devices md " +
                "JOIN device_real_time_status drts ON md.device_id = drts.device_id " +
                "WHERE drts.status = 'Idle'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                devices.add(mapResultSetToMedicalDevice(rs));
            }
        } catch (SQLException e) {
            logger.error("查询可用医疗设备失败", e);
        }
        return devices;
    }

    @Override
    public List<MedicalDevice> findDevicesByExam(int examId) {
        List<MedicalDevice> devices = new ArrayList<>();
        String sql = "SELECT md.* " +
                "FROM medical_devices md " +
                "WHERE md.device_id IN (" +
                "  SELECT device_id " +
                "  FROM device_exam_capabilities " +
                "  WHERE exam_id = ?" +
                ")";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, examId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                devices.add(mapResultSetToMedicalDevice(rs));
            }
        } catch (SQLException e) {
            logger.error("根据检查ID查询医疗设备失败", e);
        }
        return devices;
    }

    @Override
    public int countDevices() {
        String sql = "SELECT COUNT(*) FROM medical_devices";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("统计医疗设备数量失败", e);
        }
        return 0;
    }

    private MedicalDevice mapResultSetToMedicalDevice(ResultSet rs) throws SQLException {
        MedicalDevice device = new MedicalDevice();
        device.setDeviceId(rs.getInt("device_id"));
        device.setDeviceName(rs.getString("device_name"));
        device.setQuantity(rs.getInt("quantity"));
        device.setLocation(rs.getString("location"));
        device.setCreatedAt(rs.getTimestamp("created_at"));
        return device;
    }
}