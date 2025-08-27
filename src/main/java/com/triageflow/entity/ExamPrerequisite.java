package com.triageflow.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// 检查项目前置约束实体类
public class ExamPrerequisite {
    private int examId;
    private int prerequisiteExamId;

    // 关联数据
    private MedicalExam exam;
    private MedicalExam prerequisiteExam;
}