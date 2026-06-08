package com.example.codegenerator.model;

import lombok.Data;

@Data
public class ColumnInfo {
    private String columnName;
    private String fieldName;
    private int jdbcType;
    private String jdbcTypeName;
    private String javaType;
    private String javaTypeShort;
    private boolean primaryKey;
    private boolean autoIncrement;
    private boolean nullable;
    private int length;
    private String comment;
}
