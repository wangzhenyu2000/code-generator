package com.example.codegenerator.service;

import com.example.codegenerator.model.ColumnInfo;
import com.example.codegenerator.model.ConnectionTestRequest;
import com.example.codegenerator.model.ReverseRequest;
import com.example.codegenerator.model.TableInfo;
import com.example.codegenerator.util.JdbcMetadataReader;
import com.example.codegenerator.util.NamingUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReverseService {

    private final Configuration freeMarkerConfig;
    private final ZipService zipService;

    private static final List<String> REVERSE_TEMPLATES = List.of(
            "entity.java.ftl",
            "mapper.java.ftl",
            "mapper.xml.ftl",
            "service.java.ftl",
            "service-impl.java.ftl",
            "controller.java.ftl"
    );

    public List<String> testConnection(ConnectionTestRequest request) throws SQLException {
        try {
            Class.forName(request.getDriverClassName());
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Driver class not found: " + request.getDriverClassName()
                    + ". Please ensure the JDBC driver is on the classpath.");
        }
        try (Connection conn = getConnection(request.getJdbcUrl(), request.getUsername(), request.getPassword())) {
            return JdbcMetadataReader.listTables(conn, null, request.getSchemaPattern());
        }
    }

    public byte[] generate(ReverseRequest request) {
        List<TableInfo> tables = new ArrayList<>();
        try {
            Class.forName(request.getDriverClassName());
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Driver class not found: " + request.getDriverClassName());
        }
        try (Connection conn = getConnection(request.getJdbcUrl(), request.getUsername(), request.getPassword())) {
            for (String tableName : request.getTableNames()) {
                TableInfo tableInfo = JdbcMetadataReader.readTableInfo(conn, null, request.getSchemaPattern(), tableName);
                tables.add(tableInfo);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        }

        String pkg = request.getPackageName();
        String module = request.getModuleName();
        if (module != null && !module.isBlank()) {
            pkg = pkg + "." + module;
        }
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Map<String, String> files = new LinkedHashMap<>();

        for (TableInfo tableInfo : tables) {
            // Compute type flags for entity imports
            boolean hasLocalDateTime = false, hasLocalDate = false, hasLocalTime = false, hasBigDecimal = false;
            String pkType = "Long";
            for (ColumnInfo col : tableInfo.getColumns()) {
                String s = col.getJavaTypeShort();
                if ("LocalDateTime".equals(s)) hasLocalDateTime = true;
                if ("LocalDate".equals(s)) hasLocalDate = true;
                if ("LocalTime".equals(s)) hasLocalTime = true;
                if ("BigDecimal".equals(s)) hasBigDecimal = true;
                if (col.isPrimaryKey()) {
                    pkType = s;
                }
            }

            Map<String, Object> model = new HashMap<>();
            model.put("packageName", pkg);
            model.put("author", request.getAuthor() != null ? request.getAuthor() : "");
            model.put("date", today);
            model.put("tableInfo", tableInfo);
            model.put("hasLocalDateTime", hasLocalDateTime);
            model.put("hasLocalDate", hasLocalDate);
            model.put("hasLocalTime", hasLocalTime);
            model.put("hasBigDecimal", hasBigDecimal);
            model.put("pkType", pkType);

            for (String tplName : REVERSE_TEMPLATES) {
                try {
                    Template tpl = freeMarkerConfig.getTemplate("reverse/" + tplName);
                    StringWriter sw = new StringWriter();
                    tpl.process(model, sw);
                    String content = sw.toString();
                    String outputPath = resolvePath(tplName, pkg, tableInfo.getEntityName());
                    files.put(outputPath, content);
                } catch (Exception e) {
                    log.error("Failed to process template: {} for table: {}", tplName, tableInfo.getTableName(), e);
                    throw new RuntimeException("模板渲染失败: " + tplName, e);
                }
            }
        }

        try {
            return zipService.createZip(files);
        } catch (Exception e) {
            throw new RuntimeException("ZIP 打包失败", e);
        }
    }

    private Connection getConnection(String url, String username, String password) throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", username);
        props.setProperty("password", password != null ? password : "");
        props.setProperty("useInformationSchema", "true");
        props.setProperty("remarks", "true");
        return DriverManager.getConnection(url, props);
    }

    private String resolvePath(String tplName, String pkg, String entityName) {
        String pkgPath = NamingUtil.toPackagePath(pkg);
        return switch (tplName) {
            case "entity.java.ftl" -> pkgPath + "/entity/" + entityName + ".java";
            case "mapper.java.ftl" -> pkgPath + "/mapper/" + entityName + "Mapper.java";
            case "mapper.xml.ftl" -> pkgPath + "/mapper/" + entityName + "Mapper.xml";
            case "service.java.ftl" -> pkgPath + "/service/" + entityName + "Service.java";
            case "service-impl.java.ftl" -> pkgPath + "/service/impl/" + entityName + "ServiceImpl.java";
            case "controller.java.ftl" -> pkgPath + "/controller/" + entityName + "Controller.java";
            default -> entityName + "/" + tplName.replace(".ftl", "");
        };
    }
}
