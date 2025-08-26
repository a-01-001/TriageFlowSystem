package com.triageflow.dao.impl;

import com.triageflow.dao.PatientExamAssignmentDAO;
import com.triageflow.entity.PatientExamAssignment;
import com.triageflow.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class PatientExamAssignmentDAOImpl implements PatientExamAssignmentDAO {

    @Override
    public Optional<PatientExamAssignment> findById(int id) {
        String sql = "SELECT * FROM patient_exam_assignments WHERE assignment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToPatientExamAssignment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<PatientExamAssignment> findAll() {
        List<PatientExamAssignment> assignments = new ArrayList<>();
        String sql = "SELECT * FROM patient_exam_assignments";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                assignments.add(mapResultSetToPatientExamAssignment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assignments;
    }

    @Override
    public PatientExamAssignment save(PatientExamAssignment assignment) {
        String sql = "INSERT INTO patient_exam_assignments (patient_id, exam_id, status, priority, " +
                "scheduled_start_time, scheduled_end_time, actual_start_time, actual_end_time, " +
                "assigned_device_id, waiting_time_minutes, queue_position) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, assignment.getPatientId());
            stmt.setInt(2, assignment.getExamId());
            stmt.setString(3, assignment.getStatus());
            stmt.setInt(4, assignment.getPriority());
            setNullableTimestamp(stmt, 5, assignment.getScheduledStartTime());
            setNullableTimestamp(stmt, 6, assignment.getScheduledEndTime());
            setNullableTimestamp(stmt, 7, assignment.getActualStartTime());
            setNullableTimestamp(stmt, 8, assignment.getActualEndTime());
            setNullableInt(stmt, 9, assignment.getAssignedDeviceId());
            stmt.setInt(10, assignment.getWaitingTimeMinutes());
            stmt.setInt(11, assignment.getQueuePosition());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                assignment.setAssignmentId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assignment;
    }

    @Override
    public PatientExamAssignment update(PatientExamAssignment assignment) {
        String sql = "UPDATE patient_exam_assignments SET patient_id = ?, exam_id = ?, status = ?, priority = ?, " +
                "scheduled_start_time = ?, scheduled_end_time = ?, actual_start_time = ?, actual_end_time = ?, " +
                "assigned_device_id = ?, waiting_time_minutes = ?, queue_position = ? " +
                "WHERE assignment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, assignment.getPatientId());
            stmt.setInt(2, assignment.getExamId());
            stmt.setString(3, assignment.getStatus());
            stmt.setInt(4, assignment.getPriority());
            setNullableTimestamp(stmt, 5, assignment.getScheduledStartTime());
            setNullableTimestamp(stmt, 6, assignment.getScheduledEndTime());
            setNullableTimestamp(stmt, 7, assignment.getActualStartTime());
            setNullableTimestamp(stmt, 8, assignment.getActualEndTime());
            setNullableInt(stmt, 9, assignment.getAssignedDeviceId());
            stmt.setInt(10, assignment.getWaitingTimeMinutes());
            stmt.setInt(11, assignment.getQueuePosition());
            stmt.setInt(12, assignment.getAssignmentId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assignment;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM patient_exam_assignments WHERE assignment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteByPatientId(int patientId) {
        String sql = "DELETE FROM patient_exam_assignments WHERE patient_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteByExamId(int examId) {
        String sql = "DELETE FROM patient_exam_assignments WHERE exam_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, examId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteByPatientAndExam(int patientId, int examId) {
        String sql = "DELETE FROM patient_exam_assignments WHERE patient_id = ? AND exam_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            stmt.setInt(2, examId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<PatientExamAssignment> findByPatientId(int patientId) {
        List<PatientExamAssignment> assignments = new ArrayList<>();
        String sql = "SELECT * FROM patient_exam_assignments WHERE patient_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                assignments.add(mapResultSetToPatientExamAssignment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assignments;
    }

    @Override
    public List<PatientExamAssignment> findByExamId(int examId) {
        List<PatientExamAssignment> assignments = new ArrayList<>();
        String sql = "SELECT * FROM patient_exam_assignments WHERE exam_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, examId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                assignments.add(mapResultSetToPatientExamAssignment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assignments;
    }

    @Override
    public List<PatientExamAssignment> findByStatus(String status) {
        List<PatientExamAssignment> assignments = new ArrayList<>();
        String sql = "SELECT * FROM patient_exam_assignments WHERE status = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                assignments.add(mapResultSetToPatientExamAssignment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assignments;
    }

    @Override
    public boolean updateAssignmentStatus(int assignmentId, String status) {
        String sql = "UPDATE patient_exam_assignments SET status = ? WHERE assignment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, assignmentId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean scheduleExam(int assignmentId, int deviceId, Date startTime) {
        // 首先获取检查时长
        String durationSql = "SELECT duration_minutes FROM device_exam_capabilities " +
                "WHERE exam_id = (SELECT exam_id FROM patient_exam_assignments WHERE assignment_id = ?) " +
                "AND device_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement durationStmt = conn.prepareStatement(durationSql)) {
            durationStmt.setInt(1, assignmentId);
            durationStmt.setInt(2, deviceId);
            ResultSet rs = durationStmt.executeQuery();

            if (rs.next()) {
                int durationMinutes = rs.getInt("duration_minutes");
                Date endTime = new Date(startTime.getTime() + durationMinutes * 60 * 1000);

                // 更新分配记录
                String updateSql = "UPDATE patient_exam_assignments SET status = 'Scheduled', " +
                        "assigned_device_id = ?, scheduled_start_time = ?, scheduled_end_time = ? " +
                        "WHERE assignment_id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, deviceId);
                    updateStmt.setTimestamp(2, new Timestamp(startTime.getTime()));
                    updateStmt.setTimestamp(3, new Timestamp(endTime.getTime()));
                    updateStmt.setInt(4, assignmentId);
                    int rowsAffected = updateStmt.executeUpdate();
                    return rowsAffected > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<PatientExamAssignment> findPendingAssignments() {
        List<PatientExamAssignment> assignments = new ArrayList<>();
        String sql = "SELECT * FROM patient_exam_assignments WHERE status = 'Pending'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                assignments.add(mapResultSetToPatientExamAssignment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assignments;
    }

    @Override
    public int countAssignmentsByPatient(int patientId) {
        String sql = "SELECT COUNT(*) FROM patient_exam_assignments WHERE patient_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private PatientExamAssignment mapResultSetToPatientExamAssignment(ResultSet rs) throws SQLException {
        PatientExamAssignment assignment = new PatientExamAssignment();
        assignment.setAssignmentId(rs.getInt("assignment_id"));
        assignment.setPatientId(rs.getInt("patient_id"));
        assignment.setExamId(rs.getInt("exam_id"));
        assignment.setStatus(rs.getString("status"));
        assignment.setPriority(rs.getInt("priority"));
        assignment.setScheduledStartTime(rs.getTimestamp("scheduled_start_time"));
        assignment.setScheduledEndTime(rs.getTimestamp("scheduled_end_time"));
        assignment.setActualStartTime(rs.getTimestamp("actual_start_time"));
        assignment.setActualEndTime(rs.getTimestamp("actual_end_time"));
        assignment.setAssignedDeviceId(rs.getInt("assigned_device_id"));
        if (rs.wasNull()) assignment.setAssignedDeviceId(null);
        assignment.setWaitingTimeMinutes(rs.getInt("waiting_time_minutes"));
        assignment.setQueuePosition(rs.getInt("queue_position"));
        assignment.setCreatedAt(rs.getTimestamp("created_at"));
        return assignment;
    }

    private void setNullableInt(PreparedStatement stmt, int index, Integer value) throws SQLException {
        if (value != null) {
            stmt.setInt(index, value);
        } else {
            stmt.setNull(index, Types.INTEGER);
        }
    }

    private void setNullableTimestamp(PreparedStatement stmt, int index, Date value) throws SQLException {
        if (value != null) {
            stmt.setTimestamp(index, new Timestamp(value.getTime()));
        } else {
            stmt.setNull(index, Types.TIMESTAMP);
        }
    }
}