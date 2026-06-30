package com.example.codegenerator.util;

public class SqlErrorUtil {

    private SqlErrorUtil() {}

    public static String translate(String raw) {
        if (raw == null) return "数据库连接失败";
        String lower = raw.toLowerCase();
        if (lower.contains("unknown database"))                    return "数据库不存在，请检查 JDBC URL 中的数据库名";
        if (lower.contains("access denied"))                      return "用户名或密码错误";
        if (lower.contains("communications link failure")
                || lower.contains("connection refused")
                || lower.contains("timeout"))                     return "无法连接到数据库，请检查地址和端口";
        if (lower.contains("no suitable driver"))                 return "找不到数据库驱动，请检查数据库类型";
        if (lower.contains("unknown column"))                     return "表中不存在该字段: " + raw;
        if (lower.contains("table") && raw.toLowerCase().contains("doesn't exist"))
                                                                  return "表不存在: " + raw;
        return "数据库错误: " + raw;
    }
}
