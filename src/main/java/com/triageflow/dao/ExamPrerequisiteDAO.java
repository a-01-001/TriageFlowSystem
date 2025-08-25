package com.triageflow.dao;

import com.triageflow.entity.ExamPrerequisite;
import java.util.List;

public interface ExamPrerequisiteDAO {
    // 基本CRUD操作
    ExamPrerequisite getByIds(int examId, int prerequisiteExamId);
    List<ExamPrerequisite> getAll();
    boolean insert(ExamPrerequisite prerequisite);
    boolean update(ExamPrerequisite prerequisite);
    boolean delete(int examId, int prerequisiteExamId);

    // 特定查询方法
    List<ExamPrerequisite> getByExamId(int examId);
    List<ExamPrerequisite> getByPrerequisiteExamId(int prerequisiteExamId);
    boolean isPrerequisite(int examId, int prerequisiteExamId);

    // 检查方法
    boolean hasPrerequisites(int examId);
    boolean isDependent(int examId, int dependentExamId);

    // 统计方法
    int getPrerequisiteCount(int examId);
    int getDependentCount(int examId);

    // 关联数据查询
    List<ExamPrerequisite> getAllWithExam();
    List<ExamPrerequisite> getAllWithPrerequisiteExam();
}