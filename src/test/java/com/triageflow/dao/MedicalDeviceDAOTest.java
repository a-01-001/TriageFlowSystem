package com.triageflow.dao;

import com.triageflow.dao.impl.MedicalDeviceDAOImpl;
import com.triageflow.entity.MedicalDevice;
import com.triageflow.utils.DBConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MedicalDeviceDAOTest {

    private MedicalDeviceDAO medicalDeviceDAO;
    private MedicalDevice testDevice;

    @BeforeAll
    void setup() {
        medicalDeviceDAO = new MedicalDeviceDAOImpl();

        // 插入测试数据
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("INSERT IGNORE INTO medical_devices (device_id, device_name, quantity, location) VALUES (1, 'X-Ray Machine', 2, 'Room 101')");
            stmt.executeUpdate("INSERT IGNORE INTO medical_devices (device_id, device_name, quantity, location) VALUES (2, 'MRI Scanner', 1, 'Room 102')");
            stmt.executeUpdate("INSERT IGNORE INTO medical_exams (exam_id, exam_name, requires_fasting) VALUES (1, 'Chest X-Ray', FALSE)");
            stmt.executeUpdate("INSERT IGNORE INTO device_exam_capabilities (exam_id, device_id, duration_minutes) VALUES (1, 1, 15)");

        } catch (SQLException e) {
            fail("测试数据插入失败: " + e.getMessage());
        }
    }

    @AfterAll
    void cleanup() {
        // 清理测试数据
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM medical_devices WHERE device_id IN (1,2)");
            stmt.executeUpdate("DELETE FROM medical_exams WHERE exam_id = 1");
            stmt.executeUpdate("DELETE FROM device_exam_capabilities WHERE exam_id = 1 AND device_id = 1");
        } catch (SQLException e) {
            fail("测试数据清理失败: " + e.getMessage());
        }
    }

    @BeforeEach
    void init() {
        // 创建测试设备
        testDevice = new MedicalDevice();
        testDevice.setDeviceName("Test Device");
        testDevice.setQuantity(5);
        testDevice.setLocation("Test Location");
        medicalDeviceDAO.save(testDevice);
    }

    @AfterEach
    void tearDown() {
        // 清理测试数据
        if (testDevice != null && testDevice.getDeviceId() > 0) {
            medicalDeviceDAO.delete(testDevice.getDeviceId());
        }
    }

    @Test
    void testFindById() {
        Optional<MedicalDevice> foundDevice = medicalDeviceDAO.findById(testDevice.getDeviceId());
        assertTrue(foundDevice.isPresent(), "应该能找到设备");
        assertEquals(testDevice.getDeviceName(), foundDevice.get().getDeviceName(), "设备名称应该匹配");
    }

    @Test
    void testFindAll() {
        List<MedicalDevice> devices = medicalDeviceDAO.findAll();
        assertFalse(devices.isEmpty(), "设备列表不应为空");
    }

    @Test
    void testSave() {
        MedicalDevice newDevice = new MedicalDevice();
        newDevice.setDeviceName("New Test Device");
        newDevice.setQuantity(3);
        newDevice.setLocation("New Location");

        MedicalDevice savedDevice = medicalDeviceDAO.save(newDevice);
        assertTrue(savedDevice.getDeviceId() > 0, "设备ID应该大于0");

        // 清理
        medicalDeviceDAO.delete(savedDevice.getDeviceId());
    }

    @Test
    void testUpdate() {
        testDevice.setDeviceName("Updated Device Name");
        MedicalDevice updatedDevice = medicalDeviceDAO.update(testDevice);
        assertEquals("Updated Device Name", updatedDevice.getDeviceName(), "设备名称应该更新");

        Optional<MedicalDevice> foundDevice = medicalDeviceDAO.findById(testDevice.getDeviceId());
        assertTrue(foundDevice.isPresent(), "应该能找到更新后的设备");
        assertEquals("Updated Device Name", foundDevice.get().getDeviceName(), "设备名称应该已更新");
    }

    @Test
    void testDelete() {
        int deviceId = testDevice.getDeviceId();
        medicalDeviceDAO.delete(deviceId);

        Optional<MedicalDevice> foundDevice = medicalDeviceDAO.findById(deviceId);
        assertFalse(foundDevice.isPresent(), "删除后不应找到设备");

        // 防止tearDown中重复删除
        testDevice = null;
    }

    @Test
    void testFindByLocation() {
        List<MedicalDevice> devices = medicalDeviceDAO.findByLocation("Test Location");
        assertNotNull(devices, "按位置查找的设备列表不应为null");
    }

    @Test
    void testFindByName() {
        Optional<MedicalDevice> foundDevice = medicalDeviceDAO.findByName("Test Device");
        assertTrue(foundDevice.isPresent(), "应该能通过名称找到设备");
        assertEquals(testDevice.getDeviceId(), foundDevice.get().getDeviceId(), "设备ID应该匹配");
    }

    @Test
    void testFindAvailableDevices() {
        List<MedicalDevice> devices = medicalDeviceDAO.findAvailableDevices();
        assertNotNull(devices, "可用设备列表不应为null");
    }

    @Test
    void testFindDevicesByExam() {
        List<MedicalDevice> devices = medicalDeviceDAO.findDevicesByExam(1); // 假设检查项目ID为1
        assertNotNull(devices, "能执行特定检查的设备列表不应为null");
    }

    @Test
    void testCountDevices() {
        int count = medicalDeviceDAO.countDevices();
        assertTrue(count >= 1, "设备计数应该至少为1");
    }
}