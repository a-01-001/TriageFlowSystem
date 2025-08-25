package com.triageflow.entity;

import java.util.Date;
import java.util.List;

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

    // 构造方法
    public Patient() {}

    public Patient(String name, String gender, int age, String address, String phone,
                   boolean isFasting, Date arrivalTime) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.address = address;
        this.phone = phone;
        this.isFasting = isFasting;
        this.arrivalTime = arrivalTime;
    }

    // Getter和Setter方法
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public boolean isFasting() { return isFasting; }
    public void setFasting(boolean fasting) { isFasting = fasting; }

    public Date getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(Date arrivalTime) { this.arrivalTime = arrivalTime; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public List<PatientExamAssignment> getExamAssignments() { return examAssignments; }
    public void setExamAssignments(List<PatientExamAssignment> examAssignments) { this.examAssignments = examAssignments; }

    public List<MedicalExam> getPendingExams() { return pendingExams; }
    public void setPendingExams(List<MedicalExam> pendingExams) { this.pendingExams = pendingExams; }

    public List<MedicalExam> getCompletedExams() { return completedExams; }
    public void setCompletedExams(List<MedicalExam> completedExams) { this.completedExams = completedExams; }

    // 辅助方法
    public boolean hasFastingExams() {
        return pendingExams != null && pendingExams.stream()
                .anyMatch(MedicalExam::isRequiresFasting);
    }

    public int getTotalWaitTime() {
        return examAssignments != null ?
                examAssignments.stream().mapToInt(PatientExamAssignment::getWaitingTimeMinutes).sum() : 0;
    }

    @Override
    public String toString() {
        return "Patient{" + "patientId=" + patientId + ", name='" + name + '\'' +
                ", gender='" + gender + '\'' + ", age=" + age + ", isFasting=" + isFasting + '}';
    }
}