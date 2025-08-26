package com.triageflow.service;

import com.triageflow.dao.*;
import com.triageflow.entity.*;
import java.util.*;
import java.util.stream.Collectors;

public class SchedulerService {
    private final PatientDAO patientDAO;
    private final MedicalExamDAO medicalExamDAO;
    private final MedicalDeviceDAO medicalDeviceDAO;
    private final PatientExamAssignmentDAO assignmentDAO;
    private final DeviceRealTimeStatusDAO deviceStatusDAO;
    private final DeviceWorkingScheduleDAO deviceScheduleDAO;
    private final ExamPrerequisiteDAO examPrerequisiteDAO;
    private final DeviceExamCapabilityDAO deviceCapabilityDAO;

    public SchedulerService(PatientDAO patientDAO, MedicalExamDAO medicalExamDAO,
                            MedicalDeviceDAO medicalDeviceDAO, PatientExamAssignmentDAO assignmentDAO,
                            DeviceRealTimeStatusDAO deviceStatusDAO, DeviceWorkingScheduleDAO deviceScheduleDAO,
                            ExamPrerequisiteDAO examPrerequisiteDAO, DeviceExamCapabilityDAO deviceCapabilityDAO) {
        this.patientDAO = patientDAO;
        this.medicalExamDAO = medicalExamDAO;
        this.medicalDeviceDAO = medicalDeviceDAO;
        this.assignmentDAO = assignmentDAO;
        this.deviceStatusDAO = deviceStatusDAO;
        this.deviceScheduleDAO = deviceScheduleDAO;
        this.examPrerequisiteDAO = examPrerequisiteDAO;
        this.deviceCapabilityDAO = deviceCapabilityDAO;
    }

    // 核心调度算法
    public ExamSuggestion getNextExamSuggestion(int patientId) {
        // 1. 获取患者信息和待检查项目
        Patient patient = patientDAO.findById(patientId).orElseThrow();
        List<PatientExamAssignment> pendingAssignments = assignmentDAO.findByPatientId(patientId)
                .stream().filter(a -> "Pending".equals(a.getStatus())).collect(Collectors.toList());

        // 2. 过滤出当前可执行的检查（满足前置条件）
        List<MedicalExam> availableExams = new ArrayList<>();
        for (PatientExamAssignment assignment : pendingAssignments) {
            MedicalExam exam = medicalExamDAO.findById(assignment.getExamId()).orElseThrow();
            if (examPrerequisiteDAO.isPrerequisiteSatisfied(exam.getExamId(), patientId)) {
                availableExams.add(exam);
            }
        }

        // 3. 优先处理空腹检查
        List<MedicalExam> fastingExams = availableExams.stream()
                .filter(MedicalExam::isRequiresFasting)
                .collect(Collectors.toList());

        List<MedicalExam> nonFastingExams = availableExams.stream()
                .filter(exam -> !exam.isRequiresFasting())
                .collect(Collectors.toList());

        // 4. 为每个检查找到最佳设备
        ExamSuggestion bestSuggestion = null;
        double bestScore = Double.MAX_VALUE;

        // 优先处理空腹检查
        for (MedicalExam exam : fastingExams) {
            ExamSuggestion suggestion = findBestDeviceForExam(exam, patient);
            if (suggestion != null && suggestion.getEstimatedWaitTime() < bestScore) {
                bestSuggestion = suggestion;
                bestScore = suggestion.getEstimatedWaitTime();
            }
        }

        // 如果没有空腹检查或空腹检查没有合适设备，处理非空腹检查
        if (bestSuggestion == null) {
            for (MedicalExam exam : nonFastingExams) {
                ExamSuggestion suggestion = findBestDeviceForExam(exam, patient);
                if (suggestion != null && suggestion.getEstimatedWaitTime() < bestScore) {
                    bestSuggestion = suggestion;
                    bestScore = suggestion.getEstimatedWaitTime();
                }
            }
        }

        return bestSuggestion;
    }

    private ExamSuggestion findBestDeviceForExam(MedicalExam exam, Patient patient) {
        List<MedicalDevice> capableDevices = medicalDeviceDAO.findDevicesByExam(exam.getExamId());
        ExamSuggestion bestSuggestion = null;
        double bestWaitTime = Double.MAX_VALUE;

        for (MedicalDevice device : capableDevices) {
            // 检查设备是否在工作时间
            if (!deviceScheduleDAO.isDeviceWorking(device.getDeviceId(), new Date())) {
                continue;
            }

            // 检查设备状态
            Optional<DeviceRealTimeStatus> statusOpt = deviceStatusDAO.findByDeviceId(device.getDeviceId());
            if (statusOpt.isEmpty() || !"Idle".equals(statusOpt.get().getStatus())) {
                continue;
            }

            // 计算预计等待时间（考虑队列）
            double estimatedWaitTime = calculateEstimatedWaitTime(device, exam);

            if (estimatedWaitTime < bestWaitTime) {
                bestWaitTime = estimatedWaitTime;
                bestSuggestion = new ExamSuggestion(
                        patient.getPatientId(),
                        exam.getExamId(),
                        device.getDeviceId(),
                        estimatedWaitTime,
                        new Date(System.currentTimeMillis() + (long)(estimatedWaitTime * 60000))
                );
            }
        }

        return bestSuggestion;
    }

    private double calculateEstimatedWaitTime(MedicalDevice device, MedicalExam exam) {
        // 获取设备当前队列
        List<PatientExamAssignment> queue = assignmentDAO.findByStatus("Scheduled").stream()
                .filter(a -> a.getAssignedDeviceId() != null && a.getAssignedDeviceId() == device.getDeviceId())
                .sorted(Comparator.comparing(PatientExamAssignment::getScheduledStartTime))
                .collect(Collectors.toList());

        // 计算队列中所有检查的总时间
        double totalTime = 0;
        for (PatientExamAssignment assignment : queue) {
            int duration = deviceCapabilityDAO.getExamDuration(device.getDeviceId(), assignment.getExamId());
            totalTime += duration;
        }

        // 加上当前检查的时间
        int currentExamDuration = deviceCapabilityDAO.getExamDuration(device.getDeviceId(), exam.getExamId());
        totalTime += currentExamDuration;

        return totalTime;
    }

    // 更新设备状态和分配
    public boolean assignExamToDevice(ExamSuggestion suggestion) {
        try {
            // 更新设备状态
            deviceStatusDAO.updateDeviceAssignment(
                    suggestion.getDeviceId(),
                    suggestion.getPatientId(),
                    suggestion.getExamId()
            );
            deviceStatusDAO.updateDeviceStatus(suggestion.getDeviceId(), "Busy");

            // 更新检查分配状态
            Optional<PatientExamAssignment> assignmentOpt = assignmentDAO.findByPatientId(suggestion.getPatientId())
                    .stream().filter(a -> a.getExamId() == suggestion.getExamId()).findFirst();

            if (assignmentOpt.isPresent()) {
                PatientExamAssignment assignment = assignmentOpt.get();
                assignment.setStatus("Scheduled");
                assignment.setAssignedDeviceId(suggestion.getDeviceId());
                assignment.setScheduledStartTime(suggestion.getEstimatedStartTime());

                // 计算结束时间
                int duration = deviceCapabilityDAO.getExamDuration(
                        suggestion.getDeviceId(), suggestion.getExamId());
                Date endTime = new Date(
                        suggestion.getEstimatedStartTime().getTime() + duration * 60000L);
                assignment.setScheduledEndTime(endTime);

                assignmentDAO.update(assignment);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 批量调度方法
    public Map<Integer, ExamSuggestion> scheduleBatch(List<Integer> patientIds) {
        Map<Integer, ExamSuggestion> suggestions = new HashMap<>();
        for (int patientId : patientIds) {
            ExamSuggestion suggestion = getNextExamSuggestion(patientId);
            if (suggestion != null) {
                suggestions.put(patientId, suggestion);
                assignExamToDevice(suggestion);
            }
        }
        return suggestions;
    }
}

