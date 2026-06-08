package com.example.codegenerator.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ScaffoldRequest {
    @NotBlank(message = "Group ID 不能为空")
    private String groupId;

    @NotBlank(message = "Artifact ID 不能为空")
    private String artifactId;

    private String packageName;
    private String projectName;
    private String springBootVersion = "3.2.0";
    private String javaVersion = "17";
    private int port = 8080;
}
