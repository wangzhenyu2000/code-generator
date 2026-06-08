package ${packageName}.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
<#if hasLocalDateTime>
import java.time.LocalDateTime;
</#if>
<#if hasLocalDate>
import java.time.LocalDate;
</#if>
<#if hasLocalTime>
import java.time.LocalTime;
</#if>
<#if hasBigDecimal>
import java.math.BigDecimal;
</#if>

/**
 * ${tableInfo.comment!tableInfo.entityName}
 */
@Data
@TableName("${tableInfo.tableName}")
public class ${tableInfo.entityName} {
<#list tableInfo.columns as col>
    <#if col.primaryKey>
    @TableId(type = IdType.AUTO)
    </#if>
    @TableField("${col.columnName}")
    private ${col.javaTypeShort} ${col.fieldName};<#if col.comment??> // ${col.comment}</#if>
</#list>
}
