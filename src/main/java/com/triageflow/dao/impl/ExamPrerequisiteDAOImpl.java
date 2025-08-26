package com.triageflow.dao.impl;

import com.triageflow.dao.ExamPrerequisiteDAO;
import com.triageflow.entity.ExamPrerequisite;
import com.triageflow.utils.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExamPrerequisiteDAOImpl implements ExamPrerequisiteDAO {

    private static final Logger logger = LoggerFactory.getLogger(ExamPrerequisiteDAOImpl.class);

    @Override
    public Optional<ExamPrerequisite> findById(int id) {
        // 注意：ExamPrerequisite没有单独的ID字段，使用复合主键
        // 这里我们假设有一个隐藏的ID字段，但实际上应该使用findByExamId方法
        return Optional.empty();
    }

    @Override
    public List<ExamPrerequisite> findAll() {
        List<ExamPrerequisite> prerequisites = new ArrayList<>();
        String sql = "SELECT * FROM exam_prerequisites";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                prerequisites.add(mapResultSetToExamPrerequisite(rs));
            }
        } catch (SQLException e) {
            logger.error("查询所有检查前置条件失败", e);
        }
        return prerequisites;
    }

    @Override
    public ExamPrerequisite save(ExamPrerequisite prerequisite) {
        String sql = "INSERT INTO exam_prerequisites (exam_id, prerequisite_exam_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, prerequisite.getExamId());
            stmt.setInt(2, prerequisite.getPrerequisiteExamId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("保存检查前置条件失败", e);
        }
        return prerequisite;
    }

    @Override
    public ExamPrerequisite update(ExamPrerequisite prerequisite) {
        // 由于是复合主键，更新操作实际上是删除旧记录并插入新记录
        // 这里我们假设examId和prerequisiteExamId不会同时改变
        return prerequisite;
    }

    @Override
    public void delete(int id) {
        // 注意：ExamPrerequisite没有单独的ID字段，使用复合主键
        // 这里我们假设有一个隐藏的ID字段，但实际上应该使用deletePrerequisite方法
    }

    @Override
    public void deletePrerequisite(int examId, int prerequisiteExamId) {
        String sql = "DELETE FROM exam_prerequisites WHERE exam_id = ? AND prerequisite_exam_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, examId);
            stmt.setInt(2, prerequisiteExamId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("删除检查前置条件失败", e);
        }
    }

    @Override
    public List<ExamPrerequisite> findByExamId(int examId) {
        List<ExamPrerequisite> prerequisites = new ArrayList<>();
        String sql = "SELECT * FROM exam_prerequisites WHERE exam_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, examId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                prerequisites.add(mapResultSetToExamPrerequisite(rs));
            }
        } catch (SQLException e) {
            logger.error("根据检查ID查询前置条件失败", e);
        }
        return prerequisites;
    }

    @Override
    public boolean isPrerequisiteSatisfied(int examId, int patientId) {
        // 检查患者是否已完成所有前置检查
        String sql = "SELECT COUNT(*) FROM exam_prerequisites ep " +
                "LEFT JOIN patient_exam_assignments pea ON ep.prerequisite_exam_id = pea.exam_id AND pea.patient_id = ? " +
                "WHERE ep.exam_id = ? AND (pea.status IS NULL OR pea.status != 'Completed')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            stmt.setInt(2, examId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0; // 如果没有未完成的前置检查，则返回true
            }
        } catch (SQLException e) {
            logger.error("检查前置条件是否满足失败", e);
        }
        return false;
    }

    @Override
    public boolean hasPrerequisites(int examId) {
        String sql = "SELECT COUNT(*) FROM exam_prerequisites WHERE exam_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, examId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.error("检查是否有前置条件失败", e);
        }
        return false;
    }

    @Override
    public int countPrerequisites(int examId) {
        String sql = "SELECT COUNT(*) FROM exam_prerequisites WHERE exam_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, examId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("统计检查前置条件数量失败", e);
        }
        return 0;
    }

    private ExamPrerequisite mapResultSetToExamPrerequisite(ResultSet rs) throws SQLException {
        ExamPrerequisite prerequisite = new ExamPrerequisite();
        prerequisite.setExamId(rs.getInt("exam_id"));
        prerequisite.setPrerequisiteExamId(rs.getInt("prerequisite_exam_id"));
        return prerequisite;
    }
}