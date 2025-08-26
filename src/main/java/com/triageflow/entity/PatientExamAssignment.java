package com.triageflow.entity;

import java.util.Date;

// 患者检查分配实体类
public class PatientExamAssignment {
    private int assignmentId;
    private int patientId;
    private int examId;
    private String status; // "Pending", "Scheduled", "In Progress", "Completed", "Cancelled"
    private int priority;
    private Date scheduledStartTime;
    private Date scheduledEndTime;
    private Date actualStartTime;
    private Date actualEndTime;
    private Integer assignedDeviceId;
    private int waitingTimeMinutes;
    private int queuePosition;
    private Date createdAt;

    // 关联数据
    private Patient patient;
    private MedicalExam exam;
    private MedicalDevice assignedDevice;

    public PatientExamAssignment() {}

    public PatientExamAssignment(int patientId, int examId) {
        this.patientId = patientId;
        this.examId = examId;
        this.status = "Pending";
    }

    // Getter和Setter
    public int getAssignmentId() { return assignmentId; }
    public void setAssignmentId(int assignmentId) { this.assignmentId = assignmentId; }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public int getExamId() { return examId; }
    public void setExamId(int examId) { this.examId = examId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public Date getScheduledStartTime() { return scheduledStartTime; }
    public void setScheduledStartTime(Date scheduledStartTime) { this.scheduledStartTime = scheduledStartTime; }

    public Date getScheduledEndTime() { return scheduledEndTime; }
    public void setScheduledEndTime(Date scheduledEndTime) { this.scheduledEndTime = scheduledEndTime; }

    public Date getActualStartTime() { return actualStartTime; }
    public void setActualStartTime(Date actualStartTime) { this.actualStartTime = actualStartTime; }

    public Date getActualEndTime() { return actualEndTime; }
    public void setActualEndTime(Date actualEndTime) { this.actualEndTime = actualEndTime; }

    public Integer getAssignedDeviceId() { return assignedDeviceId; }
    public void setAssignedDeviceId(Integer assignedDeviceId) { this.assignedDeviceId = assignedDeviceId; }

    public int getWaitingTimeMinutes() { return waitingTimeMinutes; }
    public void setWaitingTimeMinutes(int waitingTimeMinutes) { this.waitingTimeMinutes = waitingTimeMinutes; }

    public int getQueuePosition() { return queuePosition; }
    public void setQueuePosition(int queuePosition) { this.queuePosition = queuePosition; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public MedicalExam getExam() { return exam; }
    public void setExam(MedicalExam exam) { this.exam = exam; }

    public MedicalDevice getAssignedDevice() { return assignedDevice; }
    public void setAssignedDevice(MedicalDevice assignedDevice) { this.assignedDevice = assignedDevice; }

    // 辅助方法
    public boolean isCompleted() {
        return "Completed".equals(status);
    }

    public boolean isPending() {
        return "Pending".equals(status);
    }

    public boolean isScheduled() {
        return "Scheduled".equals(status) || "In Progress".equals(status);
    }

    public int getEstimatedDuration() {
        if (assignedDevice != null && exam != null) {
            // 从关联的设备能力对象中获取时长
            for (DeviceExamCapability capability : assignedDevice.getCapabilities()) {
                if (capability.getExamId() == exam.getExamId()) {
                    return capability.getDurationMinutes();
                }
            }
        }
        // 如果没有分配设备或找不到对应能力，返回默认值
        return 0;
    }

    @Override
    public String toString() {
        return "PatientExamAssignment{" + "assignmentId=" + assignmentId + ", patientId=" + patientId +
                ", examId=" + examId + ", status='" + status + '\'' + ", priority=" + priority + '}';
    }
}