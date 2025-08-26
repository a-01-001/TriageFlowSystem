package com.triageflow.dao;

import com.triageflow.dao.impl.DeviceExamCapabilityDAOImpl;
import com.triageflow.entity.DeviceExamCapability;
import com.triageflow.utils.DBConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeviceExamCapabilityDAOTest {

    private DeviceExamCapabilityDAO capabilityDAO;
    private DeviceExamCapability testCapability;

    @BeforeAll
    void setup() {
        capabilityDAO = new DeviceExamCapabilityDAOImpl();

        // 插入测试数据
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("INSERT IGNORE INTO medical_exams (exam_id, exam_name, requires_fasting) VALUES (1, 'TestExam1', FALSE)");
            stmt.executeUpdate("INSERT IGNORE INTO medical_exams (exam_id, exam_name, requires_fasting) VALUES (2, 'TestExam2', FALSE)");
            stmt.executeUpdate("INSERT IGNORE INTO medical_devices (device_id, device_name, quantity, location) VALUES (1, 'TestDevice1', 1, 'TestLocation')");
            stmt.executeUpdate("INSERT IGNORE INTO medical_devices (device_id, device_name, quantity, location) VALUES (2, 'TestDevice1', 2, 'TestLocation')");
        } catch (SQLException e) {
            fail("测试数据插入失败: " + e.getMessage());
        }
    }

    @AfterAll
    void cleanup() {
        // 清理测试数据
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM medical_exams WHERE exam_id IN (1,2)");
            stmt.executeUpdate("DELETE FROM medical_devices WHERE device_id IN (1,2)");
            stmt.executeUpdate("DELETE FROM device_exam_capabilities WHERE device_id IN (1,2) OR exam_id IN (1,2)");
        } catch (SQLException e) {
            fail("测试数据清理失败: " + e.getMessage());
        }
    }

    @BeforeEach
    void init() {
        // 创建测试能力记录
        testCapability = new DeviceExamCapability();
        testCapability.setDeviceId(1);
        testCapability.setExamId(1);
        testCapability.setDurationMinutes(30);

        capabilityDAO.save(testCapability);
    }

    @AfterEach
    void tearDown() {
        // 清理测试数据
        if (testCapability != null && testCapability.getDeviceId() > 0 && testCapability.getExamId() > 0) {
            capabilityDAO.deleteByDeviceAndExam(testCapability.getDeviceId(), testCapability.getExamId());
        }
    }

    @Test
    void testFindAll() {
        List<DeviceExamCapability> capabilities = capabilityDAO.findAll();
        assertFalse(capabilities.isEmpty(), "能力记录列表不应为空");
    }

    @Test
    void testSave() {
        DeviceExamCapability newCapability = new DeviceExamCapability();
        newCapability.setDeviceId(2);
        newCapability.setExamId(2);
        newCapability.setDurationMinutes(45);

        DeviceExamCapability savedCapability = capabilityDAO.save(newCapability);
        assertEquals(2, savedCapability.getDeviceId(), "设备ID应该匹配");
        assertEquals(2, savedCapability.getExamId(), "检查项目ID应该匹配");

        // 清理
        capabilityDAO.deleteByDeviceAndExam(savedCapability.getDeviceId(), savedCapability.getExamId());
    }

    @Test
    void testUpdate() {
        testCapability.setDurationMinutes(60);
        DeviceExamCapability updatedCapability = capabilityDAO.update(testCapability);
        assertEquals(60, updatedCapability.getDurationMinutes(), "持续时间应该更新");

        Optional<DeviceExamCapability> foundCapability = capabilityDAO.findByDeviceAndExam(
                testCapability.getDeviceId(), testCapability.getExamId());
        assertTrue(foundCapability.isPresent(), "应该能找到更新后的能力记录");
        assertEquals(60, foundCapability.get().getDurationMinutes(), "持续时间应该已更新");
    }

    @Test
    void testDeleteByDeviceAndExam() {
        capabilityDAO.deleteByDeviceAndExam(testCapability.getDeviceId(), testCapability.getExamId());

        Optional<DeviceExamCapability> foundCapability = capabilityDAO.findByDeviceAndExam(
                testCapability.getDeviceId(), testCapability.getExamId());
        assertFalse(foundCapability.isPresent(), "删除后不应找到能力记录");

        // 防止tearDown中重复删除
        testCapability = null;
    }

    @Test
    void testFindByDeviceAndExam() {
        Optional<DeviceExamCapability> foundCapability = capabilityDAO.findByDeviceAndExam(
                testCapability.getDeviceId(), testCapability.getExamId());
        assertTrue(foundCapability.isPresent(), "应该能找到能力记录");
        assertEquals(testCapability.getDurationMinutes(), foundCapability.get().getDurationMinutes(), "持续时间应该匹配");
    }

    @Test
    void testFindByDeviceId() {
        List<DeviceExamCapability> capabilities = capabilityDAO.findByDeviceId(testCapability.getDeviceId());
        assertNotNull(capabilities, "设备能力列表不应为null");
    }

    @Test
    void testFindByExamId() {
        List<DeviceExamCapability> capabilities = capabilityDAO.findByExamId(testCapability.getExamId());
        assertNotNull(capabilities, "检查项目能力列表不应为null");
    }

    @Test
    void testGetExamDuration() {
        int duration = capabilityDAO.getExamDuration(testCapability.getDeviceId(), testCapability.getExamId());
        assertEquals(testCapability.getDurationMinutes(), duration, "检查持续时间应该匹配");
    }

    @Test
    void testCanPerformExam() {
        boolean canPerform = capabilityDAO.canPerformExam(testCapability.getDeviceId(), testCapability.getExamId());
        assertTrue(canPerform, "设备应该能执行检查");

        // 测试不能执行的情况
        boolean cannotPerform = capabilityDAO.canPerformExam(999, 999); // 假设不存在的设备ID和检查项目ID
        assertFalse(cannotPerform, "不存在的设备不能执行检查");
    }
}