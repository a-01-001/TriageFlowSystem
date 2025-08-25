package com.triageflow.dao;

import com.triageflow.entity.MedicalExam;
import java.util.List;

public interface MedicalExamDAO {
    // 基本CRUD操作
    MedicalExam getById(int examId);
    List<MedicalExam> getAll();
    boolean insert(MedicalExam exam);
    boolean update(MedicalExam exam);
    boolean delete(int examId);

    // 特定查询方法
    List<MedicalExam> getExamsByDeviceId(int deviceId);
    List<MedicalExam> getExamsRequiringFasting();
    List<MedicalExam> getExamsNotRequiringFasting();
    List<MedicalExam> getExamsByName(String name);
    int getExamCount();

    // 关联数据查询
    List<MedicalExam> getExamsWithPrerequisites();
    List<MedicalExam> getExamsWithDependencies();
    List<MedicalExam> getExamsWithCapableDevices();

    // 前置约束相关
    boolean addPrerequisite(int examId, int prerequisiteExamId);
    boolean removePrerequisite(int examId, int prerequisiteExamId);
    boolean hasPrerequisites(int examId);
    List<MedicalExam> getPrerequisites(int examId);
    List<MedicalExam> getDependentExams(int examId);
}