package com.triageflow.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
// 患者实体类
public class Patient {
    private int patientId;
    private String name;
    private String gender; // "Male", "Female", "Other"
    private int age;
    private String address;
    private String phone;
    private boolean isFasting;
    private Date arrivalTime;
    private Date createdAt;

    // 关联数据（非数据库字段，用于业务逻辑）
    private List<PatientExamAssignment> examAssignments;
    private List<MedicalExam> pendingExams;
    private List<MedicalExam> completedExams;

    public Patient(String testPatient, String male, int i, String testAddress, String number, boolean b, Date date) {
        this.name = testPatient;
        this.gender = male;
        this.age = i;
        this.address = testAddress;
        this.phone = number;
        this.isFasting = b;
        this.arrivalTime = date;
        this.createdAt = date;
        this.examAssignments = null;
        this.pendingExams = null;
        this.completedExams = null;
        this.patientId = 0;
    }


    // 辅助方法
    public boolean hasFastingExams() {
        return pendingExams != null && pendingExams.stream()
                .anyMatch(MedicalExam::isRequiresFasting);
    }

    public int getTotalWaitTime() {
        return examAssignments != null ?
                examAssignments.stream().mapToInt(PatientExamAssignment::getWaitingTimeMinutes).sum() : 0;
    }
}