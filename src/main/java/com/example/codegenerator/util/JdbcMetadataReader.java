package com.example.codegenerator.util;

import com.example.codegenerator.model.ColumnInfo;
import com.example.codegenerator.model.TableInfo;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JdbcMetadataReader {

    public static List<String> listTables(Connection conn, String catalog, String schemaPattern) throws SQLException {
        List<String> tables = new ArrayList<>();
        DatabaseMetaData meta = conn.getMetaData();
        String cat = catalog != null ? catalog : conn.getCatalog();
        try (ResultSet rs = meta.getTables(cat, schemaPattern, "%", new String[]{"TABLE"})) {
            while (rs.next()) {
                String name = rs.getString("TABLE_NAME");
                if (name != null && !name.isEmpty()) {
                    tables.add(name);
                }
            }
        }
        return tables;
    }

    public static TableInfo readTableInfo(Connection conn, String catalog, String schemaPattern, String tableName) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        String cat = catalog != null ? catalog : conn.getCatalog();

        TableInfo tableInfo = new TableInfo();
        tableInfo.setTableName(tableName);
        tableInfo.setEntityName(NamingUtil.toPascalCase(tableName));

        // Table comment
        try (ResultSet rs = meta.getTables(cat, schemaPattern, tableName, new String[]{"TABLE"})) {
            if (rs.next()) {
                String remark = rs.getString("REMARKS");
                if (remark != null && !remark.isEmpty()) {
                    tableInfo.setComment(remark);
                }
            }
        }

        // Primary keys
        List<String> pkColumns = new ArrayList<>();
        try (ResultSet rs = meta.getPrimaryKeys(cat, schemaPattern, tableName)) {
            while (rs.next()) {
                pkColumns.add(rs.getString("COLUMN_NAME"));
            }
        }

        // Columns
        try (ResultSet rs = meta.getColumns(cat, schemaPattern, tableName, "%")) {
            while (rs.next()) {
                ColumnInfo col = new ColumnInfo();
                col.setColumnName(rs.getString("COLUMN_NAME"));
                col.setFieldName(NamingUtil.safeFieldName(col.getColumnName()));
                col.setJdbcType(rs.getInt("DATA_TYPE"));
                col.setJdbcTypeName(rs.getString("TYPE_NAME"));
                col.setNullable(rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable);

                String isAutoIncrement = rs.getString("IS_AUTOINCREMENT");
                col.setAutoIncrement("YES".equalsIgnoreCase(isAutoIncrement));

                col.setLength(rs.getInt("COLUMN_SIZE"));
                col.setPrimaryKey(pkColumns.contains(col.getColumnName()));

                String remark = rs.getString("REMARKS");
                if (remark != null && !remark.isEmpty()) {
                    col.setComment(remark);
                }

                TypeMapper.TypeMapping mapping = TypeMapper.map(col.getJdbcType(), col.getJdbcTypeName());
                col.setJavaType(mapping.fullQualifiedName());
                col.setJavaTypeShort(mapping.shortName());

                tableInfo.getColumns().add(col);
            }
        }

        // If PK column java type is Integer, prefer Long for auto-increment IDs
        for (ColumnInfo col : tableInfo.getColumns()) {
            if (col.isPrimaryKey() && col.isAutoIncrement() && "Integer".equals(col.getJavaTypeShort())) {
                col.setJavaType("java.lang.Long");
                col.setJavaTypeShort("Long");
            }
        }

        return tableInfo;
    }
}
