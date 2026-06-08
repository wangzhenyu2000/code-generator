package com.example.codegenerator.controller;

import com.example.codegenerator.common.Result;
import com.example.codegenerator.model.ConnectionTestRequest;
import com.example.codegenerator.model.ReverseRequest;
import com.example.codegenerator.service.ReverseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/reverse")
@RequiredArgsConstructor
public class ReverseController {

    private final ReverseService reverseService;

    @PostMapping("/test-connection")
    public Result<List<String>> testConnection(@Valid @RequestBody ConnectionTestRequest request) {
        try {
            List<String> tables = reverseService.testConnection(request);
            return Result.ok(tables);
        } catch (SQLException e) {
            return Result.fail(500, "数据库连接失败: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return Result.fail(400, e.getMessage());
        }
    }

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generate(@Valid @RequestBody ReverseRequest request) {
        byte[] zipBytes = reverseService.generate(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"crud-code.zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(zipBytes);
    }
}
