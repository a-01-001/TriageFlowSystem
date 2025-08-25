package com.triageflow.dao;

import com.triageflow.entity.PatientExamAssignment;
import java.util.Date;
import java.util.List;

public interface PatientExamAssignmentDAO {
    // 基本CRUD操作
    PatientExamAssignment getById(int assignmentId);
    List<PatientExamAssignment> getAll();
    boolean insert(PatientExamAssignment assignment);
    boolean update(PatientExamAssignment assignment);
    boolean delete(int assignmentId);

    // 特定查询方法
    List<PatientExamAssignment> getByPatientId(int patientId);
    List<PatientExamAssignment> getByExamId(int examId);
    List<PatientExamAssignment> getByDeviceId(int deviceId);
    List<PatientExamAssignment> getByStatus(String status);
    List<PatientExamAssignment> getByPriority(int priority);
    List<PatientExamAssignment> getByScheduledTimeRange(Date startTime, Date endTime);
    List<PatientExamAssignment> getByActualTimeRange(Date startTime, Date endTime);

    // 状态相关查询
    List<PatientExamAssignment> getPendingAssignments();
    List<PatientExamAssignment> getScheduledAssignments();
    List<PatientExamAssignment> getInProgressAssignments();
    List<PatientExamAssignment> getCompletedAssignments();
    List<PatientExamAssignment> getCancelledAssignments();

    // 更新方法
    boolean updateStatus(int assignmentId, String newStatus);
    boolean updatePriority(int assignmentId, int newPriority);
    boolean updateScheduledTime(int assignmentId, Date newStartTime, Date newEndTime);
    boolean updateActualTime(int assignmentId, Date newStartTime, Date newEndTime);
    boolean updateAssignedDevice(int assignmentId, Integer newDeviceId);
    boolean updateWaitingTime(int assignmentId, int newWaitingTime);
    boolean updateQueuePosition(int assignmentId, int newPosition);

    // 统计方法
    int getAssignmentCountForPatient(int patientId);
    int getAssignmentCountForExam(int examId);
    int getAssignmentCountForDevice(int deviceId);
    int getAssignmentCountByStatus(String status);
    double getAverageWaitingTime();
    int getTotalWaitingTimeForPatient(int patientId);

    // 检查方法
    boolean isExamAssignedToPatient(int patientId, int examId);
    boolean hasPatientCompletedExam(int patientId, int examId);

    // 关联数据查询
    List<PatientExamAssignment> getAllWithPatient();
    List<PatientExamAssignment> getAllWithExam();
    List<PatientExamAssignment> getAllWithDevice();
    List<PatientExamAssignment> getAllWithAllAssociations();
}