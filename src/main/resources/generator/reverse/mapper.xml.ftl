<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${packageName}.mapper.${tableInfo.entityName}Mapper">
    <resultMap id="BaseResultMap" type="${packageName}.entity.${tableInfo.entityName}">
<#list tableInfo.columns as col>
        <#if col.primaryKey>
        <id column="${col.columnName}" property="${col.fieldName}" jdbcType="${col.jdbcTypeName}"/>
        <#else>
        <result column="${col.columnName}" property="${col.fieldName}" jdbcType="${col.jdbcTypeName}"/>
        </#if>
</#list>
    </resultMap>

    <sql id="Base_Column_List">
        <#list tableInfo.columns as col>${col.columnName}<#sep>, </#list>
    </sql>
</mapper>
