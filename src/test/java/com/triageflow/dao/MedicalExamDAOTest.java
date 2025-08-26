package com.triageflow.dao;

import com.triageflow.dao.impl.MedicalExamDAOImpl;
import com.triageflow.entity.MedicalExam;
import com.triageflow.utils.DBConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MedicalExamDAOTest {

    private MedicalExamDAO medicalExamDAO;
    private MedicalExam testExam;

    @BeforeAll
    void setup() {
        medicalExamDAO = new MedicalExamDAOImpl();

        // 插入测试数据
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("INSERT IGNORE INTO medical_exams (exam_id, exam_name, requires_fasting) VALUES (1, 'Blood Test', TRUE)");
            stmt.executeUpdate("INSERT IGNORE INTO medical_exams (exam_id, exam_name, requires_fasting) VALUES (2, 'X-Ray', FALSE)");
            stmt.executeUpdate("INSERT IGNORE INTO medical_devices (device_id, device_name, quantity, location) VALUES (1, 'X-Ray Machine', 2, 'Room 101')");
            stmt.executeUpdate("INSERT IGNORE INTO device_exam_capabilities (exam_id, device_id, duration_minutes) VALUES (2, 1, 15)");
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
            stmt.executeUpdate("DELETE FROM medical_devices WHERE device_id = 1");
            stmt.executeUpdate("DELETE FROM device_exam_capabilities WHERE exam_id = 2 AND device_id = 1");
        } catch (SQLException e) {
            fail("测试数据清理失败: " + e.getMessage());
        }
    }

    @BeforeEach
    void init() {
        // 创建测试检查项目
        testExam = new MedicalExam();
        testExam.setExamName("Test Exam");
        testExam.setRequiresFasting(false);
        medicalExamDAO.save(testExam);
    }

    @AfterEach
    void tearDown() {
        // 清理测试数据
        if (testExam != null && testExam.getExamId() > 0) {
            medicalExamDAO.delete(testExam.getExamId());
        }
    }

    @Test
    void testFindById() {
        Optional<MedicalExam> foundExam = medicalExamDAO.findById(testExam.getExamId());
        assertTrue(foundExam.isPresent(), "应该能找到检查项目");
        assertEquals(testExam.getExamName(), foundExam.get().getExamName(), "检查项目名称应该匹配");
    }

    @Test
    void testFindAll() {
        List<MedicalExam> exams = medicalExamDAO.findAll();
        assertFalse(exams.isEmpty(), "检查项目列表不应为空");
    }

    @Test
    void testSave() {
        MedicalExam newExam = new MedicalExam();
        newExam.setExamName("New Test Exam");
        newExam.setRequiresFasting(true);

        MedicalExam savedExam = medicalExamDAO.save(newExam);
        assertTrue(savedExam.getExamId() > 0, "检查项目ID应该大于0");

        // 清理
        medicalExamDAO.delete(savedExam.getExamId());
    }

    @Test
    void testUpdate() {
        testExam.setExamName("Updated Exam Name");
        MedicalExam updatedExam = medicalExamDAO.update(testExam);
        assertEquals("Updated Exam Name", updatedExam.getExamName(), "检查项目名称应该更新");

        Optional<MedicalExam> foundExam = medicalExamDAO.findById(testExam.getExamId());
        assertTrue(foundExam.isPresent(), "应该能找到更新后的检查项目");
        assertEquals("Updated Exam Name", foundExam.get().getExamName(), "检查项目名称应该已更新");
    }

    @Test
    void testDelete() {
        int examId = testExam.getExamId();
        medicalExamDAO.delete(examId);

        Optional<MedicalExam> foundExam = medicalExamDAO.findById(examId);
        assertFalse(foundExam.isPresent(), "删除后不应找到检查项目");

        // 防止tearDown中重复删除
        testExam = null;
    }

    @Test
    void testFindByName() {
        Optional<MedicalExam> foundExam = medicalExamDAO.findByName("Test Exam");
        assertTrue(foundExam.isPresent(), "应该能通过名称找到检查项目");
        assertEquals(testExam.getExamId(), foundExam.get().getExamId(), "检查项目ID应该匹配");
    }

    @Test
    void testFindFastingExams() {
        List<MedicalExam> fastingExams = medicalExamDAO.findFastingExams();
        assertNotNull(fastingExams, "需要空腹的检查项目列表不应为null");
    }

    @Test
    void testFindByDevice() {
        List<MedicalExam> exams = medicalExamDAO.findByDevice(1); // 假设设备ID为1
        assertNotNull(exams, "设备能执行的检查项目列表不应为null");
    }

    @Test
    void testCountExams() {
        int count = medicalExamDAO.countExams();
        assertTrue(count >= 1, "检查项目计数应该至少为1");
    }
}