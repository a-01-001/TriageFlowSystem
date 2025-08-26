package com.triageflow.dao;

import com.triageflow.dao.impl.DeviceRealTimeStatusDAOImpl;
import com.triageflow.entity.DeviceRealTimeStatus;
import com.triageflow.utils.DBConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeviceRealTimeStatusDAOTest {

    private DeviceRealTimeStatusDAO statusDAO;
    private DeviceRealTimeStatus testStatus;

    @BeforeAll
    void setup() {
        statusDAO = new DeviceRealTimeStatusDAOImpl();

        // 插入依赖数据
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("INSERT IGNORE INTO patients (patient_id, name, gender, age) VALUES (1, 'TestPatient', 'M', 30)");
            stmt.executeUpdate("INSERT IGNORE INTO medical_exams (exam_id, exam_name) VALUES (1, 'TestExam')");
            stmt.executeUpdate("INSERT IGNORE INTO medical_devices (device_id, device_name) VALUES (1, 'TestDevice1')");
            stmt.executeUpdate("INSERT IGNORE INTO medical_devices (device_id, device_name) VALUES (2, 'TestDevice2')");
        } catch (SQLException e) {
            fail("测试数据插入失败: " + e.getMessage());
        }
    }

    @AfterAll
    void cleanup() {
        // 清理测试数据
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM device_real_time_status WHERE device_id IN (1,2)");
            stmt.executeUpdate("DELETE FROM patients WHERE patient_id = 1");
            stmt.executeUpdate("DELETE FROM medical_exams WHERE exam_id = 1");
            stmt.executeUpdate("DELETE FROM medical_devices WHERE device_id IN (1,2)");
        } catch (SQLException e) {
            System.err.println("测试数据清理失败: " + e.getMessage());
        }
    }

    @BeforeEach
    void init() {
        // 创建测试状态记录
        testStatus = new DeviceRealTimeStatus();
        testStatus.setDeviceId(1);
        testStatus.setStatus("Idle");
        testStatus.setQueueCount(0);
        testStatus.setUtilizationRate(0.0);

        statusDAO.save(testStatus);
    }

    @AfterEach
    void tearDown() {
        // 清理测试数据
        if (testStatus != null && testStatus.getStatusId() > 0) {
            statusDAO.delete(testStatus.getStatusId());
        }
    }

    @Test
    void testFindById() {
        Optional<DeviceRealTimeStatus> foundStatus = statusDAO.findById(testStatus.getStatusId());
        assertTrue(foundStatus.isPresent(), "应该能找到状态记录");
        assertEquals(testStatus.getDeviceId(), foundStatus.get().getDeviceId(), "设备ID应该匹配");
    }

    @Test
    void testFindAll() {
        List<DeviceRealTimeStatus> statuses = statusDAO.findAll();
        assertFalse(statuses.isEmpty(), "状态记录列表不应为空");
        assertTrue(true, "应该至少有一个状态记录");
    }

    @Test
    void testSave() {
        DeviceRealTimeStatus newStatus = new DeviceRealTimeStatus();
        newStatus.setDeviceId(2);
        newStatus.setStatus("Busy");
        newStatus.setQueueCount(3);
        newStatus.setUtilizationRate(75.5);

        DeviceRealTimeStatus savedStatus = statusDAO.save(newStatus);
        assertTrue(savedStatus.getStatusId() > 0, "状态记录ID应该大于0");

        // 清理
        statusDAO.delete(savedStatus.getStatusId());
    }

    @Test
    void testUpdate() {
        testStatus.setStatus("Busy");
        DeviceRealTimeStatus updatedStatus = statusDAO.update(testStatus);
        assertEquals("Busy", updatedStatus.getStatus(), "状态应该更新");

        Optional<DeviceRealTimeStatus> foundStatus = statusDAO.findById(testStatus.getStatusId());
        assertTrue(foundStatus.isPresent(), "应该能找到更新后的状态记录");
        assertEquals("Busy", foundStatus.get().getStatus(), "状态应该已更新");
    }

    @Test
    void testDelete() {
        int statusId = testStatus.getStatusId();
        statusDAO.delete(statusId);

        Optional<DeviceRealTimeStatus> foundStatus = statusDAO.findById(statusId);
        assertFalse(foundStatus.isPresent(), "删除后不应找到状态记录");

        // 防止tearDown中重复删除
        testStatus = null;
    }

    @Test
    void testDeleteByDeviceId() {
        statusDAO.deleteByDeviceId(testStatus.getDeviceId());

        Optional<DeviceRealTimeStatus> foundStatus = statusDAO.findByDeviceId(testStatus.getDeviceId());
        assertFalse(foundStatus.isPresent(), "删除后不应找到设备的状态记录");

        // 防止tearDown中重复删除
        testStatus = null;
    }

    @Test
    void testFindByDeviceId() {
        Optional<DeviceRealTimeStatus> foundStatus = statusDAO.findByDeviceId(testStatus.getDeviceId());
        assertTrue(foundStatus.isPresent(), "应该能找到设备的状态记录");
        assertEquals(testStatus.getStatusId(), foundStatus.get().getStatusId(), "状态ID应该匹配");
    }

    @Test
    void testFindByStatus() {
        List<DeviceRealTimeStatus> statuses = statusDAO.findByStatus("Idle");
        assertNotNull(statuses, "状态为Idle的设备列表不应为null");
    }

    @Test
    void testUpdateDeviceStatus() {
        boolean updated = statusDAO.updateDeviceStatus(testStatus.getDeviceId(), "Maintenance");
        assertTrue(updated, "应该成功更新设备状态");

        Optional<DeviceRealTimeStatus> foundStatus = statusDAO.findByDeviceId(testStatus.getDeviceId());
        assertTrue(foundStatus.isPresent(), "应该能找到设备的状态记录");
        assertEquals("Maintenance", foundStatus.get().getStatus(), "设备状态应该已更新");
    }

    @Test
    void testUpdateDeviceAssignment() {
        boolean updated = statusDAO.updateDeviceAssignment(testStatus.getDeviceId(), 1, 1);
        assertTrue(updated, "应该成功更新设备分配");

        Optional<DeviceRealTimeStatus> foundStatus = statusDAO.findByDeviceId(testStatus.getDeviceId());
        assertTrue(foundStatus.isPresent(), "应该能找到设备的状态记录");
        assertEquals(Integer.valueOf(1), foundStatus.get().getCurrentPatientId(), "当前患者ID应该已更新");
        assertEquals(Integer.valueOf(1), foundStatus.get().getCurrentExamId(), "当前检查项目ID应该已更新");
    }

    @Test
    void testFindAvailableDevices() {
        List<DeviceRealTimeStatus> availableDevices = statusDAO.findAvailableDevices();
        assertNotNull(availableDevices, "可用设备列表不应为null");
    }

    @Test
    void testIsDeviceAvailable() {
        boolean isAvailable = statusDAO.isDeviceAvailable(testStatus.getDeviceId());
        assertTrue(isAvailable, "设备应该可用");

        // 更新设备状态为Busy后再次测试
        statusDAO.updateDeviceStatus(testStatus.getDeviceId(), "Busy");
        isAvailable = statusDAO.isDeviceAvailable(testStatus.getDeviceId());
        assertFalse(isAvailable, "设备不应该可用");
    }

    @Test
    void testCountByStatus() {
        int count = statusDAO.countByStatus("Idle");
        assertTrue(count >= 1, "Idle状态的设备计数应该至少为1");
    }
}