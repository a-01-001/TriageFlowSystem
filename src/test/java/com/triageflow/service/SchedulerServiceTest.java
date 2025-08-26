package com.triageflow.service;

import com.triageflow.dao.*;
import com.triageflow.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SchedulerServiceTest {

    @Mock
    private PatientDAO patientDAO;

    @Mock
    private MedicalExamDAO medicalExamDAO;

    @Mock
    private MedicalDeviceDAO medicalDeviceDAO;

    @Mock
    private PatientExamAssignmentDAO assignmentDAO;

    @Mock
    private DeviceRealTimeStatusDAO deviceStatusDAO;

    @Mock
    private DeviceWorkingScheduleDAO deviceScheduleDAO;

    @Mock
    private ExamPrerequisiteDAO examPrerequisiteDAO;

    @Mock
    private DeviceExamCapabilityDAO deviceCapabilityDAO;

    @InjectMocks
    private SchedulerService schedulerService;

    private Patient testPatient;
    private MedicalExam fastingExam;
    private MedicalExam nonFastingExam;
    private MedicalDevice capableDevice;
    private DeviceRealTimeStatus deviceStatus;
    private PatientExamAssignment pendingAssignment;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        testPatient = new Patient();
        testPatient.setPatientId(1);
        testPatient.setName("Test Patient");
        testPatient.setFasting(true);

        fastingExam = new MedicalExam();
        fastingExam.setExamId(1);
        fastingExam.setExamName("Fasting Exam");
        fastingExam.setRequiresFasting(true);

        nonFastingExam = new MedicalExam();
        nonFastingExam.setExamId(2);
        nonFastingExam.setExamName("Non-Fasting Exam");
        nonFastingExam.setRequiresFasting(false);

        capableDevice = new MedicalDevice();
        capableDevice.setDeviceId(1);
        capableDevice.setDeviceName("Test Device");
        capableDevice.setQuantity(1);
        capableDevice.setLocation("Test Location");

        deviceStatus = new DeviceRealTimeStatus();
        deviceStatus.setDeviceId(1);
        deviceStatus.setStatus("Idle");
        deviceStatus.setQueueCount(0);

        pendingAssignment = new PatientExamAssignment();
        pendingAssignment.setAssignmentId(1);
        pendingAssignment.setPatientId(1);
        pendingAssignment.setExamId(1);
        pendingAssignment.setStatus("Pending");
    }

    @Test
    void testGetNextExamSuggestion_WithFastingPatientAndFastingExam() {
        // 模拟DAO行为
        when(patientDAO.findById(anyInt())).thenReturn(Optional.of(testPatient));
        when(assignmentDAO.findByPatientId(anyInt())).thenReturn(Arrays.asList(pendingAssignment));
        when(medicalExamDAO.findById(anyInt())).thenReturn(Optional.of(fastingExam));
        when(examPrerequisiteDAO.isPrerequisiteSatisfied(anyInt(), anyInt())).thenReturn(true);
        when(medicalDeviceDAO.findDevicesByExam(anyInt())).thenReturn(Arrays.asList(capableDevice));
        when(deviceScheduleDAO.isDeviceWorking(anyInt(), any())).thenReturn(true);
        when(deviceStatusDAO.findByDeviceId(anyInt())).thenReturn(Optional.of(deviceStatus));
        when(deviceCapabilityDAO.getExamDuration(anyInt(), anyInt())).thenReturn(30);
        when(assignmentDAO.findByStatus(anyString())).thenReturn(new ArrayList<>());

        // 执行测试
        ExamSuggestion suggestion = schedulerService.getNextExamSuggestion(1);

        // 验证结果
        assertNotNull(suggestion, "应为空腹患者返回检查建议");
        assertEquals(1, suggestion.getExamId(), "应建议空腹检查");
        assertEquals(1, suggestion.getDeviceId(), "应分配可用设备");
        assertTrue(suggestion.getEstimatedWaitTime() >= 0, "等待时间应为非负数");
    }

    @Test
    void testGetNextExamSuggestion_WithNonFastingPatient() {
        // 设置非空腹患者
        testPatient.setFasting(false);

        // 模拟DAO行为
        when(patientDAO.findById(anyInt())).thenReturn(Optional.of(testPatient));
        when(assignmentDAO.findByPatientId(anyInt())).thenReturn(Arrays.asList(pendingAssignment));
        when(medicalExamDAO.findById(anyInt())).thenReturn(Optional.of(nonFastingExam));
        when(examPrerequisiteDAO.isPrerequisiteSatisfied(anyInt(), anyInt())).thenReturn(true);
        when(medicalDeviceDAO.findDevicesByExam(anyInt())).thenReturn(Arrays.asList(capableDevice));
        when(deviceScheduleDAO.isDeviceWorking(anyInt(), any())).thenReturn(true);
        when(deviceStatusDAO.findByDeviceId(anyInt())).thenReturn(Optional.of(deviceStatus));
        when(deviceCapabilityDAO.getExamDuration(anyInt(), anyInt())).thenReturn(30);
        when(assignmentDAO.findByStatus(anyString())).thenReturn(new ArrayList<>());

        // 执行测试
        ExamSuggestion suggestion = schedulerService.getNextExamSuggestion(1);

        // 验证结果
        assertNotNull(suggestion, "应为非空腹患者返回检查建议");
        assertEquals(2, suggestion.getExamId(), "应建议非空腹检查");
    }

    @Test
    void testGetNextExamSuggestion_WithUnsatisfiedPrerequisites() {
        // 模拟DAO行为 - 前置条件不满足
        when(patientDAO.findById(anyInt())).thenReturn(Optional.of(testPatient));
        when(assignmentDAO.findByPatientId(anyInt())).thenReturn(Arrays.asList(pendingAssignment));
        when(medicalExamDAO.findById(anyInt())).thenReturn(Optional.of(fastingExam));
        when(examPrerequisiteDAO.isPrerequisiteSatisfied(anyInt(), anyInt())).thenReturn(false);

        // 执行测试
        ExamSuggestion suggestion = schedulerService.getNextExamSuggestion(1);

        // 验证结果
        assertNull(suggestion, "前置条件不满足时应返回null");
    }

    @Test
    void testGetNextExamSuggestion_WithNoAvailableDevices() {
        // 模拟DAO行为 - 没有可用设备
        when(patientDAO.findById(anyInt())).thenReturn(Optional.of(testPatient));
        when(assignmentDAO.findByPatientId(anyInt())).thenReturn(Arrays.asList(pendingAssignment));
        when(medicalExamDAO.findById(anyInt())).thenReturn(Optional.of(fastingExam));
        when(examPrerequisiteDAO.isPrerequisiteSatisfied(anyInt(), anyInt())).thenReturn(true);
        when(medicalDeviceDAO.findDevicesByExam(anyInt())).thenReturn(new ArrayList<>());

        // 执行测试
        ExamSuggestion suggestion = schedulerService.getNextExamSuggestion(1);

        // 验证结果
        assertNull(suggestion, "没有可用设备时应返回null");
    }

    @Test
    void testGetNextExamSuggestion_WithBusyDevice() {
        // 设置忙碌设备状态
        deviceStatus.setStatus("Busy");

        // 模拟DAO行为
        when(patientDAO.findById(anyInt())).thenReturn(Optional.of(testPatient));
        when(assignmentDAO.findByPatientId(anyInt())).thenReturn(Arrays.asList(pendingAssignment));
        when(medicalExamDAO.findById(anyInt())).thenReturn(Optional.of(fastingExam));
        when(examPrerequisiteDAO.isPrerequisiteSatisfied(anyInt(), anyInt())).thenReturn(true);
        when(medicalDeviceDAO.findDevicesByExam(anyInt())).thenReturn(Arrays.asList(capableDevice));
        when(deviceScheduleDAO.isDeviceWorking(anyInt(), any())).thenReturn(true);
        when(deviceStatusDAO.findByDeviceId(anyInt())).thenReturn(Optional.of(deviceStatus));

        // 执行测试
        ExamSuggestion suggestion = schedulerService.getNextExamSuggestion(1);

        // 验证结果
        assertNull(suggestion, "设备忙碌时应返回null");
    }

    @Test
    void testGetNextExamSuggestion_WithDeviceNotWorking() {
        // 模拟DAO行为 - 设备不在工作时间
        when(patientDAO.findById(anyInt())).thenReturn(Optional.of(testPatient));
        when(assignmentDAO.findByPatientId(anyInt())).thenReturn(Arrays.asList(pendingAssignment));
        when(medicalExamDAO.findById(anyInt())).thenReturn(Optional.of(fastingExam));
        when(examPrerequisiteDAO.isPrerequisiteSatisfied(anyInt(), anyInt())).thenReturn(true);
        when(medicalDeviceDAO.findDevicesByExam(anyInt())).thenReturn(Arrays.asList(capableDevice));
        when(deviceScheduleDAO.isDeviceWorking(anyInt(), any())).thenReturn(false);

        // 执行测试
        ExamSuggestion suggestion = schedulerService.getNextExamSuggestion(1);

        // 验证结果
        assertNull(suggestion, "设备不在工作时间内时应返回null");
    }

    @Test
    void testAssignExamToDevice_Success() {
        // 创建测试建议
        ExamSuggestion suggestion = new ExamSuggestion();
        suggestion.setPatientId(1);
        suggestion.setExamId(1);
        suggestion.setDeviceId(1);
        suggestion.setEstimatedWaitTime(30);
        suggestion.setEstimatedStartTime(new Date());

        // 模拟DAO行为
        when(deviceStatusDAO.updateDeviceAssignment(anyInt(), any(), any())).thenReturn(true);
        when(deviceStatusDAO.updateDeviceStatus(anyInt(), anyString())).thenReturn(true);
        when(assignmentDAO.findByPatientId(anyInt())).thenReturn(Arrays.asList(pendingAssignment));
        when(deviceCapabilityDAO.getExamDuration(anyInt(), anyInt())).thenReturn(30);
        when(assignmentDAO.update(any(PatientExamAssignment.class))).thenReturn(pendingAssignment);

        // 执行测试
        boolean result = schedulerService.assignExamToDevice(suggestion);

        // 验证结果
        assertTrue(result, "分配检查到设备应成功");
        verify(deviceStatusDAO, times(1)).updateDeviceAssignment(1, 1, 1);
        verify(deviceStatusDAO, times(1)).updateDeviceStatus(1, "Busy");
        verify(assignmentDAO, times(1)).update(any(PatientExamAssignment.class));
    }

    @Test
    void testAssignExamToDevice_Failure() {
        // 创建测试建议
        ExamSuggestion suggestion = new ExamSuggestion();
        suggestion.setPatientId(1);
        suggestion.setExamId(1);
        suggestion.setDeviceId(1);

        // 模拟DAO行为 - 找不到分配记录
        when(deviceStatusDAO.updateDeviceAssignment(anyInt(), any(), any())).thenReturn(true);
        when(deviceStatusDAO.updateDeviceStatus(anyInt(), anyString())).thenReturn(true);
        when(assignmentDAO.findByPatientId(anyInt())).thenReturn(new ArrayList<>());

        // 执行测试
        boolean result = schedulerService.assignExamToDevice(suggestion);

        // 验证结果
        assertFalse(result, "找不到分配记录时应返回false");
    }

    @Test
    void testScheduleBatch_WithMultiplePatients() {
        // 创建多个患者
        Patient patient1 = testPatient;

        Patient patient2 = new Patient();
        patient2.setPatientId(2);
        patient2.setName("Test Patient 2");
        patient2.setFasting(true);

        Patient patient3 = new Patient();
        patient3.setPatientId(3);
        patient3.setName("Test Patient 3");
        patient3.setFasting(true);

        // 创建患者ID列表
        List<Integer> patientIds = Arrays.asList(1, 2, 3);

        // 创建多个分配记录
        PatientExamAssignment assignment1 = pendingAssignment;

        PatientExamAssignment assignment2 = new PatientExamAssignment();
        assignment2.setAssignmentId(2);
        assignment2.setPatientId(2);
        assignment2.setExamId(1);
        assignment2.setStatus("Pending");

        PatientExamAssignment assignment3 = new PatientExamAssignment();
        assignment3.setAssignmentId(3);
        assignment3.setPatientId(3);
        assignment3.setExamId(1);
        assignment3.setStatus("Pending");

        // 为每个患者设置特定的mock行为
        when(patientDAO.findById(1)).thenReturn(Optional.of(patient1));
        when(patientDAO.findById(2)).thenReturn(Optional.of(patient2));
        when(patientDAO.findById(3)).thenReturn(Optional.of(patient3));

        when(assignmentDAO.findByPatientId(1)).thenReturn(Arrays.asList(assignment1));
        when(assignmentDAO.findByPatientId(2)).thenReturn(Arrays.asList(assignment2));
        when(assignmentDAO.findByPatientId(3)).thenReturn(Arrays.asList(assignment3));

        // 其他通用mock设置
        when(medicalExamDAO.findById(anyInt())).thenReturn(Optional.of(fastingExam));
        when(examPrerequisiteDAO.isPrerequisiteSatisfied(anyInt(), anyInt())).thenReturn(true);
        when(medicalDeviceDAO.findDevicesByExam(anyInt())).thenReturn(Arrays.asList(capableDevice));
        when(deviceScheduleDAO.isDeviceWorking(anyInt(), any())).thenReturn(true);
        when(deviceStatusDAO.findByDeviceId(anyInt())).thenReturn(Optional.of(deviceStatus));
        when(deviceCapabilityDAO.getExamDuration(anyInt(), anyInt())).thenReturn(30);
        when(assignmentDAO.findByStatus(anyString())).thenReturn(new ArrayList<>());

        // 设备更新相关
        when(deviceStatusDAO.updateDeviceAssignment(anyInt(), any(), any())).thenReturn(true);
        when(deviceStatusDAO.updateDeviceStatus(anyInt(), anyString())).thenReturn(true);
        when(assignmentDAO.update(any(PatientExamAssignment.class))).thenReturn(pendingAssignment);

        // 调试输出
        //System.out.println("开始执行批量调度测试");

        // 执行测试
        Map<Integer, ExamSuggestion> suggestions = schedulerService.scheduleBatch(patientIds);

        // 调试输出
        //System.out.println("生成的建议数量: " + suggestions.size());
        //suggestions.forEach((id, suggestion) -> {
        //    System.out.println("患者ID: " + id + ", 建议: " + suggestion);
        //});

        // 验证结果
        assertEquals(3, suggestions.size(), "应为3个患者生成建议");
        assertTrue(suggestions.containsKey(1), "应包含患者1的建议");
        assertTrue(suggestions.containsKey(2), "应包含患者2的建议");
        assertTrue(suggestions.containsKey(3), "应包含患者3的建议");
    }
}