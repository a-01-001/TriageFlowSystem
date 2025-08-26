package com.triageflow.dao;

import com.triageflow.entity.ExamPrerequisite;
import java.util.List;

public interface ExamPrerequisiteDAO extends BaseDAO<ExamPrerequisite> {
    void deletePrerequisite(int examId, int prerequisiteExamId);
    List<ExamPrerequisite> findByExamId(int examId);

    // 核心验证方法
    boolean isPrerequisiteSatisfied(int examId, int patientId);
    boolean hasPrerequisites(int examId);

    // 扩展点
    default int countPrerequisites(int examId) { return 0; }
}