package com.triageflow.entity;

import java.util.Date;

// 建议结果类
public class ExamSuggestion {
    private int patientId;
    private int examId;
    private int deviceId;
    private double estimatedWaitTime; // 分钟
    private Date estimatedStartTime;

    public ExamSuggestion() {}

    public ExamSuggestion(int patientId, int examId, int deviceId, double estimatedWaitTime, Date estimatedStartTime) {
        this.patientId = patientId;
        this.examId = examId;
        this.deviceId = deviceId;
        this.estimatedWaitTime = estimatedWaitTime;
        this.estimatedStartTime = estimatedStartTime;
    }

    // Getter和Setter
    public int getPatientId() {
        return patientId;
    }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public int getExamId() {
        return examId;
    }
    public void setExamId(int examId) { this.examId = examId; }

    public int getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(int deviceId) { this.deviceId = deviceId; }

    public double getEstimatedWaitTime() {
        return estimatedWaitTime;
    }
    public void setEstimatedWaitTime(double estimatedWaitTime) { this.estimatedWaitTime = estimatedWaitTime; }

    public Date getEstimatedStartTime() {
        return estimatedStartTime;
    }
    public void setEstimatedStartTime(Date estimatedStartTime) { this.estimatedStartTime = estimatedStartTime; }

    @Override
    public String toString() {
        return "ExamSuggestion{" +
                "patientId=" + patientId +
                ", examId=" + examId +
                ", deviceId=" + deviceId +
                ", estimatedWaitTime=" + estimatedWaitTime +
                ", estimatedStartTime=" + estimatedStartTime +
                '}';
    }
}
