// DBConnectionTest.java
package com.triageflow.DBtest;

import com.triageflow.utils.DBConnection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.*;

public class DBConnectionTest {

    @BeforeAll
    static void setup() {
        // 确保数据库和表已经创建
        // 这里可以执行set.sql中的SQL语句来初始化数据库
    }

    @Test
    void testGetConnection() {
        try {
            Connection conn = DBConnection.getConnection();
            assertNotNull(conn, "数据库连接不应为null");
            assertFalse("数据库连接应该处于打开状态", conn.isClosed());
            conn.close();
        } catch (SQLException e) {
            fail("获取数据库连接时发生异常: " + e.getMessage());
        }
    }

    @Test
    void testCloseConnection() {
        try {
            Connection conn = DBConnection.getConnection();
            assertNotNull(conn);
            DBConnection.closeConnection(conn);
            assertTrue(conn.isClosed(), "数据库连接应该已关闭");
        } catch (SQLException e) {
            fail("关闭数据库连接时发生异常: " + e.getMessage());
        }
    }
}