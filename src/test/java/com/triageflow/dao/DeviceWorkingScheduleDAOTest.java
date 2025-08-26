// DeviceWorkingScheduleDAOTest.java
package com.triageflow.dao;

import com.triageflow.dao.impl.DeviceWorkingScheduleDAOImpl;
import com.triageflow.entity.DeviceWorkingSchedule;
import com.triageflow.utils.DBConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeviceWorkingScheduleDAOTest {

    private DeviceWorkingScheduleDAO scheduleDAO;
    private DeviceWorkingSchedule testSchedule;

    @BeforeAll
    void setup() {
        scheduleDAO = new DeviceWorkingScheduleDAOImpl();

        // 插入测试数据
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("INSERT IGNORE INTO medical_devices (device_id, device_name) VALUES (1, 'TestDevice1')");
            stmt.executeUpdate("INSERT IGNORE INTO medical_devices (device_id, device_name) VALUES (2, 'TestDevice2')");
        } catch (SQLException e) {
            fail("测试数据插入失败: " + e.getMessage());
        }
    }

    @AfterAll
    void cleanup() {
        // 清理测试数据
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM device_working_schedules WHERE device_id IN (1,2)");
            stmt.executeUpdate("DELETE FROM medical_devices WHERE device_id IN (1,2)");
        } catch (SQLException e) {
            fail("测试数据清理失败: " + e.getMessage());
        }
    }

    @BeforeEach
    void init() {
        // 获取当前日期
        Calendar calendar = Calendar.getInstance();
        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        // 将 Java 的星期日(1)到星期六(7)转换为数据库中的周一(1)到周日(7)
        int dbDayOfWeek = (currentDayOfWeek == Calendar.SUNDAY) ? 7 : currentDayOfWeek - 1;

        // 创建测试排班记录
        testSchedule = new DeviceWorkingSchedule();
        testSchedule.setDeviceId(1);
        testSchedule.setDayOfWeek(dbDayOfWeek); // 设置为当前星期几

        // 获取当前时间前后一小时的时间
        Calendar startCal = Calendar.getInstance();
        startCal.add(Calendar.HOUR_OF_DAY, -1);

        Calendar endCal = Calendar.getInstance();
        endCal.add(Calendar.HOUR_OF_DAY, 1);

        // 设置工作时间段
        testSchedule.setStartTime(new java.sql.Time(startCal.getTimeInMillis()));
        testSchedule.setEndTime(new java.sql.Time(endCal.getTimeInMillis()));
        testSchedule.setWorking(true);

        scheduleDAO.save(testSchedule);
    }

    @AfterEach
    void tearDown() {
        // 清理测试数据
        if (testSchedule != null && testSchedule.getScheduleId() > 0) {
            scheduleDAO.delete(testSchedule.getScheduleId());
        }
    }

    @Test
    void testFindById() {
        Optional<DeviceWorkingSchedule> foundSchedule = scheduleDAO.findById(testSchedule.getScheduleId());
        assertTrue(foundSchedule.isPresent(), "应该能找到排班记录");
        assertEquals(testSchedule.getDeviceId(), foundSchedule.get().getDeviceId(), "设备ID应该匹配");
    }

    @Test
    void testFindAll() {
        List<DeviceWorkingSchedule> schedules = scheduleDAO.findAll();
        assertFalse(schedules.isEmpty(), "排班记录列表不应为空");
        assertTrue(schedules.size() >= 1, "应该至少有一个排班记录");
    }

    @Test
    void testSave() {
        DeviceWorkingSchedule newSchedule = new DeviceWorkingSchedule();
        newSchedule.setDeviceId(2);
        newSchedule.setDayOfWeek(2); // 周二
        newSchedule.setStartTime(new Date(System.currentTimeMillis() - 7200000)); // 2小时前
        newSchedule.setEndTime(new Date(System.currentTimeMillis() + 7200000)); // 2小时后
        newSchedule.setWorking(true);

        DeviceWorkingSchedule savedSchedule = scheduleDAO.save(newSchedule);
        assertNotNull(savedSchedule.getScheduleId(), "保存的排班记录应该有ID");
        assertTrue(savedSchedule.getScheduleId() > 0, "排班记录ID应该大于0");

        // 清理
        scheduleDAO.delete(savedSchedule.getScheduleId());
    }

    @Test
    void testUpdate() {
        testSchedule.setDayOfWeek(3); // 改为周三
        DeviceWorkingSchedule updatedSchedule = scheduleDAO.update(testSchedule);
        assertEquals(3, updatedSchedule.getDayOfWeek(), "星期几应该更新");

        Optional<DeviceWorkingSchedule> foundSchedule = scheduleDAO.findById(testSchedule.getScheduleId());
        assertTrue(foundSchedule.isPresent(), "应该能找到更新后的排班记录");
        assertEquals(3, foundSchedule.get().getDayOfWeek(), "星期几应该已更新");
    }

    @Test
    void testDelete() {
        int scheduleId = testSchedule.getScheduleId();
        scheduleDAO.delete(scheduleId);

        Optional<DeviceWorkingSchedule> foundSchedule = scheduleDAO.findById(scheduleId);
        assertFalse(foundSchedule.isPresent(), "删除后不应找到排班记录");

        // 防止tearDown中重复删除
        testSchedule = null;
    }

    @Test
    void testDeleteByDeviceId() {
        // 创建另一个排班记录用于测试
        DeviceWorkingSchedule anotherSchedule = new DeviceWorkingSchedule();
        anotherSchedule.setDeviceId(testSchedule.getDeviceId());
        anotherSchedule.setDayOfWeek(4); // 周四
        anotherSchedule.setStartTime(new Date(System.currentTimeMillis() - 10800000)); // 3小时前
        anotherSchedule.setEndTime(new Date(System.currentTimeMillis() + 10800000)); // 3小时后
        anotherSchedule.setWorking(true);

        scheduleDAO.save(anotherSchedule);

        // 删除该设备的所有排班记录
        scheduleDAO.deleteByDeviceId(testSchedule.getDeviceId());

        // 验证是否已删除
        List<DeviceWorkingSchedule> schedules = scheduleDAO.findByDeviceId(testSchedule.getDeviceId());
        assertTrue(schedules.isEmpty(), "删除后不应找到该设备的任何排班记录");

        // 防止tearDown中重复删除
        testSchedule = null;
    }

    @Test
    void testFindByDeviceId() {
        List<DeviceWorkingSchedule> schedules = scheduleDAO.findByDeviceId(testSchedule.getDeviceId());
        assertNotNull(schedules, "设备排班列表不应为null");
        assertTrue(schedules.size() >= 1, "应该至少找到一个排班记录");
    }

    @Test
    void testIsDeviceWorking() {
        // 获取当前时间
        Date now = new Date();

        // 测试当前时间是否在工作时间内
        boolean isWorking = scheduleDAO.isDeviceWorking(testSchedule.getDeviceId(), now);
        assertTrue(isWorking, "设备应该在当前时间工作");

        // 创建一个非工作时间的测试
        Calendar futureTime = Calendar.getInstance();
        futureTime.add(Calendar.HOUR_OF_DAY, 3); // 3小时后
        Date future = futureTime.getTime();

        boolean willBeWorking = scheduleDAO.isDeviceWorking(testSchedule.getDeviceId(), future);
        assertFalse(willBeWorking, "设备不应该在3小时后工作");
    }

    @Test
    void testFindWorkingSchedules() {
        List<DeviceWorkingSchedule> schedules = scheduleDAO.findWorkingSchedules();
        assertNotNull(schedules, "工作排班列表不应为null");
        assertTrue(schedules.size() >= 1, "应该至少找到一个工作排班记录");
    }
}