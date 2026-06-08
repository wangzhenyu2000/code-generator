package com.example.codegenerator.service;

import com.example.codegenerator.model.ScaffoldRequest;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScaffoldService {

    private final Configuration freeMarkerConfig;
    private final ZipService zipService;

    private static final List<String> TEMPLATES = List.of(
            "pom.xml.ftl",
            "Application.java.ftl",
            "application.yml.ftl",
            "Result.java.ftl",
            "GlobalExceptionHandler.java.ftl",
            "MyBatisPlusConfig.java.ftl"
    );

    public byte[] generate(ScaffoldRequest req) {
        Map<String, Object> model = buildModel(req);
        Map<String, String> files = new LinkedHashMap<>();

        for (String tplName : TEMPLATES) {
            try {
                Template tpl = freeMarkerConfig.getTemplate("scaffold/" + tplName);
                StringWriter sw = new StringWriter();
                tpl.process(model, sw);
                String content = sw.toString();
                String outputPath = resolvePath(tplName, model);
                files.put(outputPath, content);
            } catch (Exception e) {
                log.error("Failed to process template: {}", tplName, e);
                throw new RuntimeException("模板渲染失败: " + tplName, e);
            }
        }

        try {
            return zipService.createZip(files);
        } catch (Exception e) {
            log.error("Failed to create ZIP", e);
            throw new RuntimeException("ZIP 打包失败", e);
        }
    }

    private Map<String, Object> buildModel(ScaffoldRequest req) {
        String packageName = req.getPackageName();
        if (packageName == null || packageName.isBlank()) {
            packageName = req.getGroupId() + "." + req.getArtifactId().replace("-", ".");
        }
        String projectName = req.getProjectName();
        if (projectName == null || projectName.isBlank()) {
            projectName = req.getArtifactId();
        }
        String basePackage = packageName;
        int lastDot = packageName.lastIndexOf('.');
        if (lastDot > 0) {
            basePackage = packageName.substring(0, lastDot);
        }

        Map<String, Object> model = new HashMap<>();
        model.put("groupId", req.getGroupId());
        model.put("artifactId", req.getArtifactId());
        model.put("packageName", packageName);
        model.put("basePackage", basePackage);
        model.put("projectName", projectName);
        model.put("springBootVersion", req.getSpringBootVersion());
        model.put("javaVersion", req.getJavaVersion());
        model.put("port", req.getPort());
        model.put("packagePath", packageName.replace('.', '/'));
        return model;
    }

    private String resolvePath(String tplName, Map<String, Object> model) {
        String projectName = (String) model.get("projectName");
        String packagePath = (String) model.get("packagePath");

        return switch (tplName) {
            case "pom.xml.ftl" -> projectName + "/pom.xml";
            case "Application.java.ftl" -> projectName + "/src/main/java/" + packagePath + "/" + projectName.substring(0, 1).toUpperCase() + projectName.substring(1) + "Application.java";
            case "application.yml.ftl" -> projectName + "/src/main/resources/application.yml";
            case "Result.java.ftl" -> projectName + "/src/main/java/" + packagePath + "/common/Result.java";
            case "GlobalExceptionHandler.java.ftl" -> projectName + "/src/main/java/" + packagePath + "/common/GlobalExceptionHandler.java";
            case "MyBatisPlusConfig.java.ftl" -> projectName + "/src/main/java/" + packagePath + "/config/MyBatisPlusConfig.java";
            default -> projectName + "/" + tplName.replace(".ftl", "");
        };
    }
}
