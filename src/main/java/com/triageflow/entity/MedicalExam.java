package com.triageflow.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
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

    // 辅助方法
    public boolean hasPrerequisites() {
        return prerequisites != null && !prerequisites.isEmpty();
    }
}