package com.triageflow.dao;

import com.triageflow.entity.PatientExamAssignment;
import java.util.List;

public interface SchedulingDAO {
    // 调度相关方法
    List<PatientExamAssignment> schedulePatientExams(int patientId);
    boolean rescheduleAssignment(int assignmentId);
    boolean cancelAssignment(int assignmentId);

    // 队列管理
    boolean addToQueue(int assignmentId, int deviceId);
    boolean removeFromQueue(int assignmentId);
    boolean moveUpInQueue(int assignmentId);
    boolean moveDownInQueue(int assignmentId);
    int getQueuePosition(int assignmentId);
    int getQueueSize(int deviceId);

    // 时间估计
    int estimateWaitTime(int assignmentId);
    int estimateExamDuration(int deviceId, int examId);
    int estimateTotalExamTime(int patientId);

    // 设备分配
    int findBestDeviceForExam(int examId);
    boolean assignDeviceToExam(int assignmentId, int deviceId);
    boolean releaseDeviceFromExam(int deviceId);

    // 优先级管理
    boolean updateAssignmentPriority(int assignmentId, int newPriority);
    List<PatientExamAssignment> getHighPriorityAssignments(int threshold);

    // 统计和报告
    int getScheduledAssignmentsCount();
    int getCompletedAssignmentsCount();
    double getAverageCompletionTime();
    double getDeviceUtilizationRate(int deviceId);
    List<PatientExamAssignment> getOverdueAssignments();
}