package com.example.codegenerator.util;

import java.math.BigDecimal;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class TypeMapper {

    private static final Map<Integer, TypeMapping> MAPPINGS = new HashMap<>();

    static {
        MAPPINGS.put(Types.VARCHAR, new TypeMapping("java.lang.String", "String"));
        MAPPINGS.put(Types.CHAR, new TypeMapping("java.lang.String", "String"));
        MAPPINGS.put(Types.LONGVARCHAR, new TypeMapping("java.lang.String", "String"));
        MAPPINGS.put(Types.LONGNVARCHAR, new TypeMapping("java.lang.String", "String"));
        MAPPINGS.put(Types.NVARCHAR, new TypeMapping("java.lang.String", "String"));
        MAPPINGS.put(Types.NCHAR, new TypeMapping("java.lang.String", "String"));
        MAPPINGS.put(Types.CLOB, new TypeMapping("java.lang.String", "String"));
        MAPPINGS.put(Types.NCLOB, new TypeMapping("java.lang.String", "String"));

        MAPPINGS.put(Types.TINYINT, new TypeMapping("java.lang.Integer", "Integer"));
        MAPPINGS.put(Types.SMALLINT, new TypeMapping("java.lang.Integer", "Integer"));
        MAPPINGS.put(Types.INTEGER, new TypeMapping("java.lang.Integer", "Integer"));

        MAPPINGS.put(Types.BIGINT, new TypeMapping("java.lang.Long", "Long"));

        MAPPINGS.put(Types.FLOAT, new TypeMapping("java.lang.Float", "Float"));
        MAPPINGS.put(Types.REAL, new TypeMapping("java.lang.Float", "Float"));

        MAPPINGS.put(Types.DOUBLE, new TypeMapping("java.lang.Double", "Double"));

        MAPPINGS.put(Types.DECIMAL, new TypeMapping("java.math.BigDecimal", "BigDecimal"));
        MAPPINGS.put(Types.NUMERIC, new TypeMapping("java.math.BigDecimal", "BigDecimal"));

        MAPPINGS.put(Types.BOOLEAN, new TypeMapping("java.lang.Boolean", "Boolean"));
        MAPPINGS.put(Types.BIT, new TypeMapping("java.lang.Boolean", "Boolean"));

        MAPPINGS.put(Types.DATE, new TypeMapping("java.time.LocalDate", "LocalDate"));
        MAPPINGS.put(Types.TIME, new TypeMapping("java.time.LocalTime", "LocalTime"));
        MAPPINGS.put(Types.TIMESTAMP, new TypeMapping("java.time.LocalDateTime", "LocalDateTime"));

        MAPPINGS.put(Types.BINARY, new TypeMapping("byte[]", "byte[]"));
        MAPPINGS.put(Types.VARBINARY, new TypeMapping("byte[]", "byte[]"));
        MAPPINGS.put(Types.LONGVARBINARY, new TypeMapping("byte[]", "byte[]"));
        MAPPINGS.put(Types.BLOB, new TypeMapping("byte[]", "byte[]"));
    }

    public static TypeMapping map(int jdbcType, String jdbcTypeName) {
        TypeMapping mapping = MAPPINGS.get(jdbcType);
        if (mapping != null) return mapping;

        // Heuristic for MySQL-specific type names
        if (jdbcTypeName != null) {
            String upper = jdbcTypeName.toUpperCase();
            if (upper.contains("INT") && !upper.equals("INT")) {
                if (upper.contains("BIG")) return MAPPINGS.get(Types.BIGINT);
                return MAPPINGS.get(Types.INTEGER);
            }
            if (upper.contains("TEXT") || upper.contains("VARCHAR") || upper.contains("CHAR")) {
                return MAPPINGS.get(Types.VARCHAR);
            }
            if (upper.contains("DATE") || upper.contains("TIME")) {
                if (upper.contains("TIMESTAMP") || upper.equals("DATETIME")) {
                    return MAPPINGS.get(Types.TIMESTAMP);
                }
                return MAPPINGS.get(Types.DATE);
            }
            if (upper.contains("BLOB") || upper.contains("BINARY")) {
                return MAPPINGS.get(Types.BLOB);
            }
        }
        return new TypeMapping("java.lang.String", "String");
    }

    public record TypeMapping(String fullQualifiedName, String shortName) {}
}
