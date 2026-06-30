package com.example.codegenerator.model;

import lombok.Data;

@Data
public class ConnectionConfig {
    private Long id;
    private String name;
    private String dbType;
    private String driverClassName;
    private String jdbcUrl;
    private String username;
    private String password;
    private String schemaPattern;
}
