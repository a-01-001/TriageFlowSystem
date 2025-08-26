// PatientDAOTest.java
package com.triageflow.dao;

import com.triageflow.dao.impl.PatientDAOImpl;
import com.triageflow.entity.Patient;
import com.triageflow.utils.DBConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PatientDAOTest {

    private PatientDAO patientDAO;
    private Patient testPatient;

    @BeforeAll
    void setup() {
        patientDAO = new PatientDAOImpl();

        // 插入依赖数据
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("INSERT IGNORE INTO medical_exams (exam_id, exam_name) VALUES (1, 'TestExam')");
            stmt.executeUpdate("INSERT IGNORE INTO medical_devices (device_id, device_name) VALUES (1, 'TestDevice1')");
            stmt.executeUpdate("INSERT IGNORE INTO medical_devices (device_id, device_name) VALUES (2, 'TestDevice2')");

        } catch (SQLException e) {
            fail("测试数据清理失败: " + e.getMessage());
        }
    }

    @AfterAll
    void cleanup() {
        // 清理测试数据
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM patients WHERE name IN ('Test Patient', 'New Test Patient', 'Updated Name')");
            stmt.executeUpdate("DELETE FROM medical_exams WHERE exam_id = 1");
            stmt.executeUpdate("DELETE FROM medical_devices WHERE device_id IN (1,2)");
        } catch (SQLException e) {
            System.err.println("测试数据清理失败: " + e.getMessage());
        }
    }

    @BeforeEach
    void init() {
        // 创建测试患者
        testPatient = new Patient(
                "Test Patient",
                "Male",
                30,
                "Test Address",
                "1234567890",
                false,
                new Date()
        );
        patientDAO.save(testPatient);
    }

    @AfterEach
    void tearDown() {
        // 清理测试数据
        if (testPatient != null && testPatient.getPatientId() > 0) {
            patientDAO.delete(testPatient.getPatientId());
        }
    }

    @Test
    void testFindById() {
        Optional<Patient> foundPatient = patientDAO.findById(testPatient.getPatientId());
        assertTrue(foundPatient.isPresent(), "应该能找到患者");
        assertEquals(testPatient.getName(), foundPatient.get().getName(), "患者名称应该匹配");
    }

    @Test
    void testFindAll() {
        List<Patient> patients = patientDAO.findAll();
        assertFalse(patients.isEmpty(), "患者列表不应为空");
    }

    @Test
    void testSave() {
        Patient newPatient = new Patient(
                "New Test Patient",
                "Female",
                25,
                "New Address",
                "0987654321",
                true,
                new Date()
        );

        Patient savedPatient = patientDAO.save(newPatient);
        assertTrue(savedPatient.getPatientId() > 0, "患者ID应该大于0");

        // 清理
        patientDAO.delete(savedPatient.getPatientId());
    }

    @Test
    void testUpdate() {
        testPatient.setName("Updated Name");
        Patient updatedPatient = patientDAO.update(testPatient);
        assertEquals("Updated Name", updatedPatient.getName(), "患者名称应该更新");

        Optional<Patient> foundPatient = patientDAO.findById(testPatient.getPatientId());
        assertTrue(foundPatient.isPresent(), "应该能找到更新后的患者");
        assertEquals("Updated Name", foundPatient.get().getName(), "患者名称应该已更新");
    }

    @Test
    void testDelete() {
        int patientId = testPatient.getPatientId();
        patientDAO.delete(patientId);

        Optional<Patient> foundPatient = patientDAO.findById(patientId);
        assertFalse(foundPatient.isPresent(), "删除后不应找到患者");

        // 防止tearDown中重复删除
        testPatient = null;
    }

    @Test
    void testFindByName() {
        Optional<Patient> foundPatient = patientDAO.findByName("Test Patient");
        assertTrue(foundPatient.isPresent(), "应该能通过名称找到患者");
        assertEquals(testPatient.getPatientId(), foundPatient.get().getPatientId(), "患者ID应该匹配");
    }

    @Test
    void testFindFastingPatients() {
        List<Patient> fastingPatients = patientDAO.findFastingPatients();
        // 根据测试数据，可能为空或包含患者
        assertNotNull(fastingPatients, "空腹患者列表不应为null");
    }

    @Test
    void testUpdateFastingStatus() {
        boolean updated = patientDAO.updateFastingStatus(testPatient.getPatientId(), true);
        assertTrue(updated, "应该成功更新空腹状态");

        Optional<Patient> foundPatient = patientDAO.findById(testPatient.getPatientId());
        assertTrue(foundPatient.isPresent(), "应该能找到患者");
        assertTrue(foundPatient.get().isFasting(), "患者空腹状态应该为true");
    }

    @Test
    void testFindPatientsWithPendingExams() {
        List<Patient> patients = patientDAO.findPatientsWithPendingExams();
        assertNotNull(patients, "有待检查项目的患者列表不应为null");
    }

    @Test
    void testCountPatients() {
        int count = patientDAO.countPatients();
        assertTrue(count >= 1, "患者计数应该至少为1");
    }
}