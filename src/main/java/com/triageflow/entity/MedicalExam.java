package com.triageflow.entity;

import java.util.Date;
import java.util.List;

// 检查项目实体类
public class MedicalExam {
    private int examId;
    private String examName;
    private boolean requiresFasting;
    private Date createdAt;

    // 关联数据
    private List<MedicalDevice> capableDevices;
    private List<MedicalExam> prerequisites;
    private List<MedicalExam> dependentExams;

    public MedicalExam() {}

    public MedicalExam(String examName, boolean requiresFasting) {
        this.examName = examName;
        this.requiresFasting = requiresFasting;
    }

    // Getter和Setter
    public int getExamId() { return examId; }
    public void setExamId(int examId) { this.examId = examId; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }

    public boolean isRequiresFasting() { return requiresFasting; }
    public void setRequiresFasting(boolean requiresFasting) { this.requiresFasting = requiresFasting; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public List<MedicalDevice> getCapableDevices() { return capableDevices; }
    public void setCapableDevices(List<MedicalDevice> capableDevices) { this.capableDevices = capableDevices; }

    public List<MedicalExam> getPrerequisites() { return prerequisites; }
    public void setPrerequisites(List<MedicalExam> prerequisites) { this.prerequisites = prerequisites; }

    public List<MedicalExam> getDependentExams() { return dependentExams; }
    public void setDependentExams(List<MedicalExam> dependentExams) { this.dependentExams = dependentExams; }

    // 辅助方法
    public boolean hasPrerequisites() {
        return prerequisites != null && !prerequisites.isEmpty();
    }

    @Override
    public String toString() {
        return "MedicalExam{" + "examId=" + examId + ", examName='" + examName + '\'' +
                ", requiresFasting=" + requiresFasting + '}';
    }
}