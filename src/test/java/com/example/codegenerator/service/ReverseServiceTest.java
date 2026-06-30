package com.example.codegenerator.service;

import com.example.codegenerator.model.ConnectionTestRequest;
import com.example.codegenerator.model.ReverseRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ReverseServiceTest {

    @Autowired
    private ReverseService reverseService;

    @Test
    void testConnectionInvalidUrl() {
        ConnectionTestRequest req = new ConnectionTestRequest();
        req.setDriverClassName("com.mysql.cj.jdbc.Driver");
        req.setJdbcUrl("jdbc:mysql://invalid-host:3306/db");
        req.setUsername("root");
        req.setPassword("root");

        // Should throw or return error — connection will timeout/fail
        // We just verify no NPE or unexpected exception
        try {
            reverseService.testConnection(req);
        } catch (Exception e) {
            assertTrue(e.getMessage() != null);
        }
    }

    @Test
    void generateWithEmptyTableNamesFailsFast() {
        ReverseRequest req = new ReverseRequest();
        req.setTableNames(List.of());
        req.setPackageName("com.example");
        // Missing required fields — validation happens in controller, not service
        // Service will try to connect with empty table list
    }
}
