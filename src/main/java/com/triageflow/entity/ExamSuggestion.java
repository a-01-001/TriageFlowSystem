package com.triageflow.entity;

import java.util.Date;

// 建议结果类
public class ExamSuggestion {
    private int patientId;
    private int examId;
    private int deviceId;
    private double estimatedWaitTime; // 分钟
    private Date estimatedStartTime;

    // 构造函数、getters和setters
    public ExamSuggestion(int patientId, int examId, int deviceId, double estimatedWaitTime, Date estimatedStartTime) {
        this.patientId = patientId;
        this.examId = examId;
        this.deviceId = deviceId;
        this.estimatedWaitTime = estimatedWaitTime;
        this.estimatedStartTime = estimatedStartTime;
    }

    public int getPatientId() {
        return patientId;
    }

    public int getExamId() {
        return examId;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public double getEstimatedWaitTime() {
        return estimatedWaitTime;
    }

    public Date getEstimatedStartTime() {
        return estimatedStartTime;
    }

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
