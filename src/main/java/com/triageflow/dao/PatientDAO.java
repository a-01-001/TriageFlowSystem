package com.triageflow.dao;

import com.triageflow.entity.Patient;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PatientDAO extends BaseDAO<Patient> {
    // 核心查询方法
    List<Patient> findByArrivalDate(Date date);
    Optional<Patient> findByName(String name);
    List<Patient> findFastingPatients();

    // 状态管理
    boolean updateFastingStatus(int patientId, boolean isFasting);

    // 扩展点
    default List<Patient> findPatientsWithPendingExams() { return List.of(); }
    default int countPatients() { return 0; }
}