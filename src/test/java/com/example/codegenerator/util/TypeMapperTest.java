package com.example.codegenerator.util;

import org.junit.jupiter.api.Test;

import java.sql.Types;

import static org.junit.jupiter.api.Assertions.*;

class TypeMapperTest {

    @Test
    void stringTypes() {
        assertEquals("String", TypeMapper.map(Types.VARCHAR, "VARCHAR").shortName());
        assertEquals("String", TypeMapper.map(Types.CHAR, "CHAR").shortName());
        assertEquals("String", TypeMapper.map(Types.LONGVARCHAR, "LONGVARCHAR").shortName());
        assertEquals("String", TypeMapper.map(Types.CLOB, "CLOB").shortName());
    }

    @Test
    void integerTypes() {
        assertEquals("Integer", TypeMapper.map(Types.TINYINT, "TINYINT").shortName());
        assertEquals("Integer", TypeMapper.map(Types.SMALLINT, "SMALLINT").shortName());
        assertEquals("Integer", TypeMapper.map(Types.INTEGER, "INTEGER").shortName());
    }

    @Test
    void longType() {
        assertEquals("Long", TypeMapper.map(Types.BIGINT, "BIGINT").shortName());
    }

    @Test
    void decimalTypes() {
        assertEquals("BigDecimal", TypeMapper.map(Types.DECIMAL, "DECIMAL").shortName());
        assertEquals("BigDecimal", TypeMapper.map(Types.NUMERIC, "NUMERIC").shortName());
    }

    @Test
    void booleanTypes() {
        assertEquals("Boolean", TypeMapper.map(Types.BOOLEAN, "BOOLEAN").shortName());
        assertEquals("Boolean", TypeMapper.map(Types.BIT, "BIT").shortName());
    }

    @Test
    void dateTimeTypes() {
        assertEquals("LocalDate", TypeMapper.map(Types.DATE, "DATE").shortName());
        assertEquals("LocalTime", TypeMapper.map(Types.TIME, "TIME").shortName());
        assertEquals("LocalDateTime", TypeMapper.map(Types.TIMESTAMP, "TIMESTAMP").shortName());
    }

    @Test
    void binaryTypes() {
        assertEquals("byte[]", TypeMapper.map(Types.BLOB, "BLOB").shortName());
        assertEquals("byte[]", TypeMapper.map(Types.BINARY, "BINARY").shortName());
    }

    @Test
    void unknownFallsBackToString() {
        assertEquals("String", TypeMapper.map(Types.OTHER, "UNKNOWN_TYPE").shortName());
        assertEquals("java.lang.String", TypeMapper.map(Types.OTHER, "UNKNOWN_TYPE").fullQualifiedName());
    }

    @Test
    void heuristicBigInt() {
        assertEquals("Long", TypeMapper.map(Types.OTHER, "BIGINT UNSIGNED").shortName());
    }

    @Test
    void heuristicText() {
        assertEquals("String", TypeMapper.map(Types.OTHER, "MEDIUMTEXT").shortName());
    }
}
