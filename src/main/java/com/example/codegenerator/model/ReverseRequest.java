package com.example.codegenerator.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ReverseRequest {
    @NotBlank(message = "Driver class name 不能为空")
    private String driverClassName;
    @NotBlank(message = "JDBC URL 不能为空")
    private String jdbcUrl;
    @NotBlank(message = "用户名不能为空")
    private String username;
    private String password;
    private String schemaPattern;

    @NotEmpty(message = "至少选择一张表")
    private List<String> tableNames;

    @NotBlank(message = "Package name 不能为空")
    private String packageName;
    private String author;
    private String moduleName;
}
