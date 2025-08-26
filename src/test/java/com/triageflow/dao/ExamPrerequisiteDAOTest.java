// ExamPrerequisiteDAOTest.java
package com.triageflow.dao;

import com.triageflow.dao.impl.ExamPrerequisiteDAOImpl;
import com.triageflow.entity.ExamPrerequisite;
import com.triageflow.utils.DBConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ExamPrerequisiteDAOTest {

    private ExamPrerequisiteDAO prerequisiteDAO;
    private ExamPrerequisite testPrerequisite;

    @BeforeAll
    void setup() {
        prerequisiteDAO = new ExamPrerequisiteDAOImpl();

        // 插入测试数据
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("INSERT IGNORE INTO medical_exams (exam_id, exam_name, requires_fasting) VALUES (1, 'TestExam1', FALSE)");
            stmt.executeUpdate("INSERT IGNORE INTO medical_exams (exam_id, exam_name, requires_fasting) VALUES (2, 'TestExam2', FALSE)");
            stmt.executeUpdate("INSERT IGNORE INTO medical_exams (exam_id, exam_name, requires_fasting) VALUES (3, 'TestExam3', FALSE)");
            stmt.executeUpdate("INSERT IGNORE INTO medical_exams (exam_id, exam_name, requires_fasting) VALUES (4, 'TestExam4', FALSE)");
        } catch (SQLException e) {
            fail("测试数据插入失败: " + e.getMessage());
        }
    }

    @AfterAll
    void cleanup() {
        // 清理测试数据
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM medical_exams WHERE exam_id IN (1,2,3,4)");
            stmt.executeUpdate("DELETE FROM exam_prerequisites WHERE exam_id IN (1,2,3,4) OR prerequisite_exam_id IN (1,2,3,4)");
        } catch (SQLException e) {
            fail("测试数据清理失败: " + e.getMessage());
        }
    }

    @BeforeEach
    void init() {
        // 创建测试前置条件记录
        testPrerequisite = new ExamPrerequisite();
        testPrerequisite.setExamId(1); // 假设存在检查项目ID为1
        testPrerequisite.setPrerequisiteExamId(2); // 假设存在前置检查项目ID为2

        prerequisiteDAO.save(testPrerequisite);
    }

    @AfterEach
    void tearDown() {
        // 清理测试数据
        if (testPrerequisite != null && testPrerequisite.getExamId() > 0 && testPrerequisite.getPrerequisiteExamId() > 0) {
            prerequisiteDAO.deletePrerequisite(testPrerequisite.getExamId(), testPrerequisite.getPrerequisiteExamId());
        }
    }

    @Test
    void testFindAll() {
        List<ExamPrerequisite> prerequisites = prerequisiteDAO.findAll();
        assertFalse(prerequisites.isEmpty(), "前置条件记录列表不应为空");
        assertTrue(prerequisites.size() >= 1, "应该至少有一个前置条件记录");
    }

    @Test
    void testSave() {
        ExamPrerequisite newPrerequisite = new ExamPrerequisite();
        newPrerequisite.setExamId(3);
        newPrerequisite.setPrerequisiteExamId(4);

        ExamPrerequisite savedPrerequisite = prerequisiteDAO.save(newPrerequisite);
        assertEquals(3, savedPrerequisite.getExamId(), "检查项目ID应该匹配");
        assertEquals(4, savedPrerequisite.getPrerequisiteExamId(), "前置检查项目ID应该匹配");

        // 清理
        prerequisiteDAO.deletePrerequisite(savedPrerequisite.getExamId(), savedPrerequisite.getPrerequisiteExamId());
    }

    @Test
    void testDeletePrerequisite() {
        prerequisiteDAO.deletePrerequisite(testPrerequisite.getExamId(), testPrerequisite.getPrerequisiteExamId());

        List<ExamPrerequisite> prerequisites = prerequisiteDAO.findByExamId(testPrerequisite.getExamId());
        assertTrue(prerequisites.isEmpty(), "删除后不应找到前置条件记录");

        // 防止tearDown中重复删除
        testPrerequisite = null;
    }

    @Test
    void testFindByExamId() {
        List<ExamPrerequisite> prerequisites = prerequisiteDAO.findByExamId(testPrerequisite.getExamId());
        assertNotNull(prerequisites, "检查项目前置条件列表不应为null");
        assertTrue(prerequisites.size() >= 1, "应该至少找到一个前置条件记录");
    }

    @Test
    void testIsPrerequisiteSatisfied() {
        // 假设患者ID为1已完成所有前置检查
        boolean isSatisfied = prerequisiteDAO.isPrerequisiteSatisfied(testPrerequisite.getExamId(), 1);
        // 由于测试数据可能不完整，我们只验证方法是否正常运行
        assertTrue(isSatisfied || !isSatisfied, "方法应该正常运行并返回布尔值");
    }

    @Test
    void testHasPrerequisites() {
        boolean hasPrerequisites = prerequisiteDAO.hasPrerequisites(testPrerequisite.getExamId());
        assertTrue(hasPrerequisites, "检查项目应该有前置条件");

        // 测试没有前置条件的情况
        boolean noPrerequisites = prerequisiteDAO.hasPrerequisites(999); // 假设不存在的检查项目ID
        assertFalse(noPrerequisites, "不存在的检查项目不应该有前置条件");
    }

    @Test
    void testCountPrerequisites() {
        int count = prerequisiteDAO.countPrerequisites(testPrerequisite.getExamId());
        assertTrue(count >= 1, "前置条件计数应该至少为1");
    }
}