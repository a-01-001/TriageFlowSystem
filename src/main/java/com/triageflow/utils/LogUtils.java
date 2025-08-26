package com.triageflow.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtils {

    /**
     * 获取指定类的Logger实例
     * @param clazz 指定的类
     * @return Logger实例
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    /**
     * 格式化日志消息，可用于复杂日志记录
     * @param format 格式字符串
     * @param args 参数
     * @return 格式化后的字符串
     */
    public static String formatMessage(String format, Object... args) {
        try {
            return String.format(format, args);
        } catch (Exception e) {
            return format;
        }
    }
}