package com.triageflow.dao;

import com.triageflow.entity.MedicalExam;
import java.util.List;
import java.util.Optional;

public interface MedicalExamDAO extends BaseDAO<MedicalExam> {
    // 核心查询方法
    Optional<MedicalExam> findByName(String name);
    List<MedicalExam> findFastingExams();

    // 扩展点
    default List<MedicalExam> findByDevice(int deviceId) { return List.of(); }
    default int countExams() { return 0; }
}