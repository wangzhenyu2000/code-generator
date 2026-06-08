package com.example.codegenerator.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConnectionTestRequest {
    @NotBlank(message = "Driver class name 不能为空")
    private String driverClassName;
    @NotBlank(message = "JDBC URL 不能为空")
    private String jdbcUrl;
    @NotBlank(message = "用户名不能为空")
    private String username;
    private String password;
    private String schemaPattern;
}
