// PatientExamAssignmentDAOTest.java
package com.triageflow.dao;

import com.triageflow.dao.impl.PatientExamAssignmentDAOImpl;
import com.triageflow.entity.PatientExamAssignment;
import com.triageflow.utils.DBConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PatientExamAssignmentDAOTest {

    private PatientExamAssignmentDAO assignmentDAO;
    private PatientExamAssignment testAssignment;

    @BeforeAll
    void setup() {
        assignmentDAO = new PatientExamAssignmentDAOImpl();

        // 插入依赖数据
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("INSERT IGNORE INTO patients (patient_id, name, gender, age, arrival_time) " +
                    "VALUES (1, 'TestPatient1', 'Male', 30, NOW())");
            stmt.executeUpdate("INSERT IGNORE INTO patients (patient_id, name, gender, age, arrival_time) " +
                    "VALUES (2, 'TestPatient2', 'Female', 25, NOW())");
            stmt.executeUpdate("INSERT IGNORE INTO device_exam_capabilities (device_id, exam_id, duration_minutes) " +
                    "VALUES (1, 1, 30), (2, 2, 45)");
            stmt.executeUpdate("INSERT IGNORE INTO medical_exams (exam_id, exam_name) VALUES (1, 'TestExam1')");
            stmt.executeUpdate("INSERT IGNORE INTO medical_exams (exam_id, exam_name) VALUES (2, 'TestExam2')");
            stmt.executeUpdate("INSERT IGNORE INTO medical_devices (device_id, device_name) VALUES (1, 'TestDevice1')");
            stmt.executeUpdate("INSERT IGNORE INTO medical_devices (device_id, device_name) VALUES (2, 'TestDevice2')");

            // 如果设备需要工作时间，确保当前时间在设备工作时间内
            java.sql.Time now = new java.sql.Time(System.currentTimeMillis());
            java.sql.Time oneHourBefore = new java.sql.Time(System.currentTimeMillis() - 3600000);
            java.sql.Time oneHourAfter = new java.sql.Time(System.currentTimeMillis() + 3600000);

            // 获取当前星期几 (1=周一, 7=周日)
            Calendar cal = Calendar.getInstance();
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            int adjustedDayOfWeek = (dayOfWeek == Calendar.SUNDAY) ? 7 : dayOfWeek - 1;

            stmt.executeUpdate("INSERT IGNORE INTO device_working_schedules (device_id, day_of_week, start_time, end_time, is_working) " +
                    "VALUES (1, " + adjustedDayOfWeek + ", '" + oneHourBefore + "', '" + oneHourAfter + "', TRUE)");
            stmt.executeUpdate("INSERT IGNORE INTO device_exam_capabilities (device_id, exam_id, duration_minutes) " +
                    "VALUES (1, 1, 30)");
        } catch (SQLException e) {
            fail("测试数据清理失败: " + e.getMessage());
        }
    }

    @AfterAll
    void cleanup() {
        // 清理测试数据
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM patient_exam_assignments WHERE patient_id = 1");
            stmt.executeUpdate("DELETE FROM patients WHERE patient_id IN (1,2)");
            stmt.executeUpdate("DELETE FROM medical_exams WHERE exam_id IN (1,2)");
            stmt.executeUpdate("DELETE FROM medical_devices WHERE device_id IN (1,2)");
            stmt.executeUpdate("DELETE FROM device_exam_capabilities WHERE device_id IN (1,2)");
            stmt.executeUpdate("DELETE FROM device_working_schedules WHERE device_id IN (1,2)");
        } catch (SQLException e) {
            System.err.println("测试数据清理失败: " + e.getMessage());
        }
    }

    @BeforeEach
    void init() {
        // 创建测试分配记录
        testAssignment = new PatientExamAssignment();
        testAssignment.setPatientId(1); // 假设存在患者ID为1
        testAssignment.setExamId(1);    // 假设存在检查项目ID为1
        testAssignment.setStatus("Pending");
        testAssignment.setPriority(1);
        testAssignment.setWaitingTimeMinutes(0);
        testAssignment.setQueuePosition(0);

        assignmentDAO.save(testAssignment);
    }

    @AfterEach
    void tearDown() {
        // 清理测试数据
        if (testAssignment != null && testAssignment.getAssignmentId() > 0) {
            assignmentDAO.delete(testAssignment.getAssignmentId());
        }
    }

    @Test
    void testFindById() {
        Optional<PatientExamAssignment> foundAssignment = assignmentDAO.findById(testAssignment.getAssignmentId());
        assertTrue(foundAssignment.isPresent(), "应该能找到分配记录");
        assertEquals(testAssignment.getPatientId(), foundAssignment.get().getPatientId(), "患者ID应该匹配");
    }

    @Test
    void testFindAll() {
        List<PatientExamAssignment> assignments = assignmentDAO.findAll();
        assertFalse(assignments.isEmpty(), "分配记录列表不应为空");
    }

    @Test
    void testSave() {
        PatientExamAssignment newAssignment = new PatientExamAssignment();
        newAssignment.setPatientId(1);
        newAssignment.setExamId(2);
        newAssignment.setStatus("Pending");
        newAssignment.setPriority(2);
        newAssignment.setWaitingTimeMinutes(5);
        newAssignment.setQueuePosition(1);

        PatientExamAssignment savedAssignment = assignmentDAO.save(newAssignment);
        assertTrue(savedAssignment.getAssignmentId() > 0, "分配记录ID应该大于0");

        // 清理
        assignmentDAO.delete(savedAssignment.getAssignmentId());
    }

    @Test
    void testUpdate() {
        testAssignment.setStatus("Scheduled");
        PatientExamAssignment updatedAssignment = assignmentDAO.update(testAssignment);
        assertEquals("Scheduled", updatedAssignment.getStatus(), "分配状态应该更新");

        Optional<PatientExamAssignment> foundAssignment = assignmentDAO.findById(testAssignment.getAssignmentId());
        assertTrue(foundAssignment.isPresent(), "应该能找到更新后的分配记录");
        assertEquals("Scheduled", foundAssignment.get().getStatus(), "分配状态应该已更新");
    }

    @Test
    void testDelete() {
        int assignmentId = testAssignment.getAssignmentId();
        assignmentDAO.delete(assignmentId);

        Optional<PatientExamAssignment> foundAssignment = assignmentDAO.findById(assignmentId);
        assertFalse(foundAssignment.isPresent(), "删除后不应找到分配记录");

        // 防止tearDown中重复删除
        testAssignment = null;
    }

    @Test
    void testDeleteByPatientId() {
        // 创建另一个分配记录用于测试
        PatientExamAssignment anotherAssignment = new PatientExamAssignment();
        anotherAssignment.setPatientId(testAssignment.getPatientId());
        anotherAssignment.setExamId(2);
        anotherAssignment.setStatus("Pending");
        anotherAssignment.setPriority(1);
        anotherAssignment.setWaitingTimeMinutes(0);
        anotherAssignment.setQueuePosition(0);

        assignmentDAO.save(anotherAssignment);

        // 删除该患者的所有分配记录
        assignmentDAO.deleteByPatientId(testAssignment.getPatientId());

        // 验证是否已删除
        List<PatientExamAssignment> assignments = assignmentDAO.findByPatientId(testAssignment.getPatientId());
        assertTrue(assignments.isEmpty(), "删除后不应找到该患者的任何分配记录");

        // 防止tearDown中重复删除
        testAssignment = null;
    }

    @Test
    void testDeleteByExamId() {
        // 创建另一个分配记录用于测试
        PatientExamAssignment anotherAssignment = new PatientExamAssignment();
        anotherAssignment.setPatientId(2);
        anotherAssignment.setExamId(testAssignment.getExamId());
        anotherAssignment.setStatus("Pending");
        anotherAssignment.setPriority(1);
        anotherAssignment.setWaitingTimeMinutes(0);
        anotherAssignment.setQueuePosition(0);

        assignmentDAO.save(anotherAssignment);

        // 删除该检查项目的所有分配记录
        assignmentDAO.deleteByExamId(testAssignment.getExamId());

        // 验证是否已删除
        List<PatientExamAssignment> assignments = assignmentDAO.findByExamId(testAssignment.getExamId());
        assertTrue(assignments.isEmpty(), "删除后不应找到该检查项目的任何分配记录");

        // 防止tearDown中重复删除
        testAssignment = null;
    }

    @Test
    void testDeleteByPatientAndExam() {
        // 删除特定的分配记录
        assignmentDAO.deleteByPatientAndExam(testAssignment.getPatientId(), testAssignment.getExamId());

        // 验证是否已删除
        Optional<PatientExamAssignment> foundAssignment = assignmentDAO.findById(testAssignment.getAssignmentId());
        assertFalse(foundAssignment.isPresent(), "删除后不应找到该分配记录");

        // 防止tearDown中重复删除
        testAssignment = null;
    }

    @Test
    void testFindByPatientId() {
        List<PatientExamAssignment> assignments = assignmentDAO.findByPatientId(testAssignment.getPatientId());
        assertNotNull(assignments, "患者分配记录列表不应为null");
    }

    @Test
    void testFindByExamId() {
        List<PatientExamAssignment> assignments = assignmentDAO.findByExamId(testAssignment.getExamId());
        assertNotNull(assignments, "检查项目分配记录列表不应为null");
    }

    @Test
    void testFindByStatus() {
        List<PatientExamAssignment> assignments = assignmentDAO.findByStatus("Pending");
        assertNotNull(assignments, "状态为Pending的分配记录列表不应为null");
    }

    @Test
    void testUpdateAssignmentStatus() {
        boolean updated = assignmentDAO.updateAssignmentStatus(testAssignment.getAssignmentId(), "Completed");
        assertTrue(updated, "应该成功更新分配状态");

        Optional<PatientExamAssignment> foundAssignment = assignmentDAO.findById(testAssignment.getAssignmentId());
        assertTrue(foundAssignment.isPresent(), "应该能找到分配记录");
        assertEquals("Completed", foundAssignment.get().getStatus(), "分配状态应该已更新");
    }

    @Test
    void testScheduleExam() {
        //System.out.println("测试前分配记录状态: " + testAssignment.getStatus());
        //System.out.println("测试前分配记录ID: " + testAssignment.getAssignmentId());

        // 先检查设备是否在工作时间内
        //DeviceWorkingScheduleDAO scheduleDAO = new DeviceWorkingScheduleDAOImpl();
        //boolean isDeviceWorking = scheduleDAO.isDeviceWorking(1, new Date());
        //System.out.println("设备是否在工作时间内: " + isDeviceWorking);

        // 检查分配记录是否存在
        //Optional<PatientExamAssignment> beforeTest = assignmentDAO.findById(testAssignment.getAssignmentId());
        //System.out.println("测试前能否找到分配记录: " + beforeTest.isPresent());

        boolean scheduled = assignmentDAO.scheduleExam(testAssignment.getAssignmentId(), 1, new Date());
        assertTrue(scheduled, "应该成功安排检查");

        Optional<PatientExamAssignment> foundAssignment = assignmentDAO.findById(testAssignment.getAssignmentId());
        assertTrue(foundAssignment.isPresent(), "应该能找到分配记录");
        assertEquals("Scheduled", foundAssignment.get().getStatus(), "分配状态应该已更新为Scheduled");
    }

    @Test
    void testFindPendingAssignments() {
        List<PatientExamAssignment> assignments = assignmentDAO.findPendingAssignments();
        assertNotNull(assignments, "待处理分配记录列表不应为null");
    }

    @Test
    void testCountAssignmentsByPatient() {
        int count = assignmentDAO.countAssignmentsByPatient(testAssignment.getPatientId());
        assertTrue(count >= 1, "患者分配记录计数应该至少为1");
    }
}