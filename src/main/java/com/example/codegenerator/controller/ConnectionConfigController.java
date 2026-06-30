package com.example.codegenerator.controller;

import com.example.codegenerator.common.Result;
import com.example.codegenerator.model.ConnectionConfig;
import com.example.codegenerator.service.ConnectionConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/connection-config")
@RequiredArgsConstructor
@Profile("!test")
public class ConnectionConfigController {

    private final ConnectionConfigService service;

    @GetMapping
    public Result<List<ConnectionConfig>> list() {
        return Result.ok(service.list());
    }

    @PostMapping
    public Result<ConnectionConfig> save(@RequestBody ConnectionConfig config) {
        return Result.ok(service.save(config));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return Result.ok(null);
    }
}
