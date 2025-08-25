package com.triageflow.entity;

// 检查项目前置约束实体类
public class ExamPrerequisite {
    private int examId;
    private int prerequisiteExamId;

    // 关联数据
    private MedicalExam exam;
    private MedicalExam prerequisiteExam;

    public ExamPrerequisite() {}

    public ExamPrerequisite(int examId, int prerequisiteExamId) {
        this.examId = examId;
        this.prerequisiteExamId = prerequisiteExamId;
    }

    // Getter和Setter
    public int getExamId() { return examId; }
    public void setExamId(int examId) { this.examId = examId; }

    public int getPrerequisiteExamId() { return prerequisiteExamId; }
    public void setPrerequisiteExamId(int prerequisiteExamId) { this.prerequisiteExamId = prerequisiteExamId; }

    public MedicalExam getExam() { return exam; }
    public void setExam(MedicalExam exam) { this.exam = exam; }

    public MedicalExam getPrerequisiteExam() { return prerequisiteExam; }
    public void setPrerequisiteExam(MedicalExam prerequisiteExam) { this.prerequisiteExam = prerequisiteExam; }

    @Override
    public String toString() {
        return "ExamPrerequisite{" + "examId=" + examId + ", prerequisiteExamId=" + prerequisiteExamId + '}';
    }
}