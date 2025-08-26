package com.triageflow.dao.impl;

import com.triageflow.dao.MedicalExamDAO;
import com.triageflow.entity.MedicalExam;
import com.triageflow.utils.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MedicalExamDAOImpl implements MedicalExamDAO {

    private static final Logger logger = LoggerFactory.getLogger(MedicalExamDAOImpl.class);

    @Override
    public Optional<MedicalExam> findById(int id) {
        String sql = "SELECT * FROM medical_exams WHERE exam_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToMedicalExam(rs));
            }
        } catch (SQLException e) {
            logger.error("根据ID查询医疗检查失败", e);
        }
        return Optional.empty();
    }

    @Override
    public List<MedicalExam> findAll() {
        List<MedicalExam> exams = new ArrayList<>();
        String sql = "SELECT * FROM medical_exams";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                exams.add(mapResultSetToMedicalExam(rs));
            }
        } catch (SQLException e) {
            logger.error("查询所有医疗检查失败", e);
        }
        return exams;
    }

    @Override
    public MedicalExam save(MedicalExam exam) {
        String sql = "INSERT INTO medical_exams (exam_name, requires_fasting) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, exam.getExamName());
            stmt.setBoolean(2, exam.isRequiresFasting());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                exam.setExamId(rs.getInt(1));
            }
        } catch (SQLException e) {
            logger.error("保存医疗检查失败", e);
        }
        return exam;
    }

    @Override
    public MedicalExam update(MedicalExam exam) {
        String sql = "UPDATE medical_exams SET exam_name = ?, requires_fasting = ? WHERE exam_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, exam.getExamName());
            stmt.setBoolean(2, exam.isRequiresFasting());
            stmt.setInt(3, exam.getExamId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("更新医疗检查失败", e);
        }
        return exam;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM medical_exams WHERE exam_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("删除医疗检查失败", e);
        }
    }

    @Override
    public Optional<MedicalExam> findByName(String name) {
        String sql = "SELECT * FROM medical_exams WHERE exam_name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToMedicalExam(rs));
            }
        } catch (SQLException e) {
            logger.error("根据名称查询医疗检查失败", e);
        }
        return Optional.empty();
    }

    @Override
    public List<MedicalExam> findFastingExams() {
        List<MedicalExam> exams = new ArrayList<>();
        String sql = "SELECT * FROM medical_exams WHERE requires_fasting = TRUE";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                exams.add(mapResultSetToMedicalExam(rs));
            }
        } catch (SQLException e) {
            logger.error("查询需要空腹的医疗检查失败", e);
        }
        return exams;
    }

    @Override
    public List<MedicalExam> findByDevice(int deviceId) {
        List<MedicalExam> exams = new ArrayList<>();
        String sql = "SELECT medical_exams.* " +
                "FROM medical_exams " +
                "INNER JOIN device_exam_capabilities " +
                "ON medical_exams.exam_id = device_exam_capabilities.exam_id " +
                "WHERE device_exam_capabilities.device_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, deviceId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                exams.add(mapResultSetToMedicalExam(rs));
            }
        } catch (SQLException e) {
            logger.error("根据设备ID查询医疗检查失败", e);
        }
        return exams;
    }

    @Override
    public int countExams() {
        String sql = "SELECT COUNT(*) FROM medical_exams";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("统计医疗检查数量失败", e);
        }
        return 0;
    }

    private MedicalExam mapResultSetToMedicalExam(ResultSet rs) throws SQLException {
        MedicalExam exam = new MedicalExam();
        exam.setExamId(rs.getInt("exam_id"));
        exam.setExamName(rs.getString("exam_name"));
        exam.setRequiresFasting(rs.getBoolean("requires_fasting"));
        exam.setCreatedAt(rs.getTimestamp("created_at"));
        return exam;
    }
}