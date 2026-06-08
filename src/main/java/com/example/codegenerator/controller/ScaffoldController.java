package com.example.codegenerator.controller;

import com.example.codegenerator.model.ScaffoldRequest;
import com.example.codegenerator.service.ScaffoldService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scaffold")
@RequiredArgsConstructor
public class ScaffoldController {

    private final ScaffoldService scaffoldService;

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generate(@Valid @RequestBody ScaffoldRequest request) {
        byte[] zipBytes = scaffoldService.generate(request);
        String filename = (request.getArtifactId() != null ? request.getArtifactId() : "project") + "-scaffold.zip";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(zipBytes);
    }
}
