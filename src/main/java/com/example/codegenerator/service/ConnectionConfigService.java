package com.example.codegenerator.service;

import com.example.codegenerator.model.ConnectionConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Service
@RequiredArgsConstructor
@Profile("!test")
public class ConnectionConfigService {

    private final JdbcTemplate jdbc;

    private static final RowMapper<ConnectionConfig> ROW_MAPPER = (rs, rowNum) -> {
        ConnectionConfig c = new ConnectionConfig();
        c.setId(rs.getLong("id"));
        c.setName(rs.getString("name"));
        c.setDbType(rs.getString("db_type"));
        c.setDriverClassName(rs.getString("driver_class_name"));
        c.setJdbcUrl(rs.getString("jdbc_url"));
        c.setUsername(rs.getString("username"));
        c.setPassword(rs.getString("password"));
        c.setSchemaPattern(rs.getString("schema_pattern"));
        return c;
    };

    public List<ConnectionConfig> list() {
        return jdbc.query("SELECT * FROM connection_config ORDER BY id", ROW_MAPPER);
    }

    public ConnectionConfig save(ConnectionConfig config) {
        if (config.getId() != null) {
            jdbc.update(
                "UPDATE connection_config SET name=?, db_type=?, driver_class_name=?, jdbc_url=?, username=?, password=?, schema_pattern=? WHERE id=?",
                config.getName(), config.getDbType(), config.getDriverClassName(),
                config.getJdbcUrl(), config.getUsername(), config.getPassword(),
                config.getSchemaPattern(), config.getId()
            );
            return config;
        }
        GeneratedKeyHolder holder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO connection_config (name, db_type, driver_class_name, jdbc_url, username, password, schema_pattern) VALUES (?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, config.getName());
            ps.setString(2, config.getDbType());
            ps.setString(3, config.getDriverClassName());
            ps.setString(4, config.getJdbcUrl());
            ps.setString(5, config.getUsername());
            ps.setString(6, config.getPassword() != null ? config.getPassword() : "");
            ps.setString(7, config.getSchemaPattern() != null ? config.getSchemaPattern() : "");
            return ps;
        }, holder);
        config.setId(holder.getKey().longValue());
        return config;
    }

    public void delete(Long id) {
        jdbc.update("DELETE FROM connection_config WHERE id=?", id);
    }
}
