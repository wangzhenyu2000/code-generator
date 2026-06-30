package com.example.codegenerator.service;

import com.example.codegenerator.model.ScaffoldRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = "spring.freemarker.template-loader-path=classpath:/generator")
@ActiveProfiles("test")
class ScaffoldServiceTest {

    @Autowired
    private ScaffoldService scaffoldService;

    @Test
    void generateProducesValidZip() throws Exception {
        ScaffoldRequest req = new ScaffoldRequest();
        req.setGroupId("com.example");
        req.setArtifactId("demo");
        req.setPackageName("com.example.demo");
        req.setSpringBootVersion("3.2.0");
        req.setJavaVersion("17");
        req.setPort(8080);

        byte[] zip = scaffoldService.generate(req);
        assertNotNull(zip);
        assertTrue(zip.length > 1000);

        // Verify ZIP structure
        java.util.Set<String> entries = new java.util.HashSet<>();
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zip))) {
            var entry = zis.getNextEntry();
            while (entry != null) {
                entries.add(entry.getName());
                zis.closeEntry();
                entry = zis.getNextEntry();
            }
        }

        assertTrue(entries.contains("demo/pom.xml"));
        assertTrue(entries.contains("demo/src/main/java/com/example/demo/DemoApplication.java"));
        assertTrue(entries.contains("demo/src/main/resources/application.yml"));
        assertTrue(entries.contains("demo/src/main/java/com/example/demo/common/Result.java"));
        assertTrue(entries.contains("demo/src/main/java/com/example/demo/common/GlobalExceptionHandler.java"));
        assertTrue(entries.contains("demo/src/main/java/com/example/demo/config/MyBatisPlusConfig.java"));
    }

    @Test
    void generateWithDefaults() throws Exception {
        ScaffoldRequest req = new ScaffoldRequest();
        req.setGroupId("org.test");
        req.setArtifactId("my-project");
        // Leave packageName null — should be auto-generated

        byte[] zip = scaffoldService.generate(req);
        assertNotNull(zip);
        assertTrue(zip.length > 1000);
    }
}
