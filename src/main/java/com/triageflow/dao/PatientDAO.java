package com.triageflow.dao;

import com.triageflow.entity.Patient;
import java.util.Date;
import java.util.List;

public interface PatientDAO {
    // 基本CRUD操作
    Patient getById(int patientId);
    List<Patient> getAll();
    boolean insert(Patient patient);
    boolean update(Patient patient);
    boolean delete(int patientId);

    // 特定查询方法
    List<Patient> getPatientsByExamId(int examId);
    List<Patient> getFastingPatients();
    List<Patient> getNonFastingPatients();
    List<Patient> getPatientsByGender(String gender);
    List<Patient> getPatientsByAgeRange(int minAge, int maxAge);
    List<Patient> getPatientsByArrivalDate(Date date);
    List<Patient> getPatientsByName(String name);
    int getPatientCount();

    // 状态相关查询
    List<Patient> getPatientsWithPendingExams();
    List<Patient> getPatientsWithCompletedExams();
    List<Patient> getPatientsWithInProgressExams();

    // 关联数据查询
    List<Patient> getPatientsWithExamAssignments();
    List<Patient> getPatientsWithAllData();

    // 更新方法
    boolean updateFastingStatus(int patientId, boolean isFasting);
    boolean updateArrivalTime(int patientId, Date newArrivalTime);
}