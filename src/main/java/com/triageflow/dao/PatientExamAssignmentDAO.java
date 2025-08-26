package com.triageflow.dao;

import com.triageflow.entity.PatientExamAssignment;
import java.util.Date;
import java.util.List;

public interface PatientExamAssignmentDAO extends BaseDAO<PatientExamAssignment> {
    // 批量删除
    void deleteByPatientId(int patientId);
    void deleteByExamId(int examId);
    void deleteByPatientAndExam(int patientId, int examId);

    // 核心查询方法
    List<PatientExamAssignment> findByPatientId(int patientId);
    List<PatientExamAssignment> findByExamId(int examId);
    List<PatientExamAssignment> findByStatus(String status);

    // 分配管理
    boolean updateAssignmentStatus(int assignmentId, String status);
    boolean scheduleExam(int assignmentId, int deviceId, Date startTime);

    // 扩展点：统计和查询
    default List<PatientExamAssignment> findPendingAssignments() { return List.of(); }
    default int countAssignmentsByPatient(int patientId) { return 0; }
}