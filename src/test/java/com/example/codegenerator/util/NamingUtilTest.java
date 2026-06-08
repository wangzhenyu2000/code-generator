package com.example.codegenerator.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NamingUtilTest {

    @Test
    void toCamelCaseSimple() {
        assertEquals("sysUser", NamingUtil.toCamelCase("sys_user"));
        assertEquals("createTime", NamingUtil.toCamelCase("create_time"));
    }

    @Test
    void toCamelCaseSingleWord() {
        assertEquals("name", NamingUtil.toCamelCase("name"));
        assertEquals("id", NamingUtil.toCamelCase("id"));
    }

    @Test
    void toCamelCaseConsecutiveUnderscores() {
        assertEquals("aB", NamingUtil.toCamelCase("a__b"));
        assertEquals("fieldName", NamingUtil.toCamelCase("field__name"));
    }

    @Test
    void toCamelCaseLeadingTrailingUnderscore() {
        assertEquals("field", NamingUtil.toCamelCase("_field"));
        assertEquals("field", NamingUtil.toCamelCase("field_"));
    }

    @Test
    void toPascalCase() {
        assertEquals("SysUser", NamingUtil.toPascalCase("sys_user"));
        assertEquals("CreateTime", NamingUtil.toPascalCase("create_time"));
        assertEquals("Name", NamingUtil.toPascalCase("name"));
    }

    @Test
    void safeFieldNameReserved() {
        assertEquals("classField", NamingUtil.safeFieldName("class"));
        assertEquals("defaultField", NamingUtil.safeFieldName("default"));
        assertEquals("staticField", NamingUtil.safeFieldName("static"));
    }

    @Test
    void safeFieldNameNormal() {
        assertEquals("userName", NamingUtil.safeFieldName("user_name"));
        assertEquals("age", NamingUtil.safeFieldName("age"));
    }

    @Test
    void toPackagePath() {
        assertEquals("com/example/demo", NamingUtil.toPackagePath("com.example.demo"));
    }

    @Test
    void lowerFirst() {
        assertEquals("sysUser", NamingUtil.lowerFirst("SysUser"));
        assertEquals("a", NamingUtil.lowerFirst("A"));
        assertEquals("", NamingUtil.lowerFirst(""));
        assertEquals("", NamingUtil.lowerFirst(null));
    }

    @Test
    void emptyAndNull() {
        assertEquals("", NamingUtil.toCamelCase(""));
        assertEquals("", NamingUtil.toCamelCase(null));
        assertEquals("", NamingUtil.toPascalCase(""));
        assertEquals("", NamingUtil.safeFieldName(""));
    }
}
