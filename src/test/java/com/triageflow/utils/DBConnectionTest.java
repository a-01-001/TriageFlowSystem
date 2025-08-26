// DBConnectionTest.java
package com.triageflow.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

public class DBConnectionTest {

    @BeforeAll
    static void setup() {
        // 执行set.sql脚本初始化数据库
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // 读取SQL脚本文件
            String sqlScript = Files.readString(Paths.get("src/main/resources/MySQL/set.sql"));

            // 分割SQL语句（假设语句以分号结尾）
            String[] sqlStatements = sqlScript.split(";");

            // 执行每条SQL语句
            for (String sql : sqlStatements) {
                sql = sql.trim();
                if (!sql.isEmpty()) {
                    stmt.execute(sql);
                }
            }

            System.out.println("数据库初始化完成");
        } catch (SQLException | IOException e) {
            System.err.println("初始化数据库失败: " + e.getMessage());
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @Test
    void testGetConnection() {
        try {
            Connection conn = DBConnection.getConnection();
            assertNotNull(conn, "数据库连接不应为null");
            Assertions.assertFalse(conn.isClosed(), "数据库连接应该处于打开状态");
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