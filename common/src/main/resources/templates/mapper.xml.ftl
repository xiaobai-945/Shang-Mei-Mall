<?xml version="1.0" encoding="UTF-8" ?>

<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${package.Mapper}.${table.mapperName}">

    <!-- 插入操作 -->
    <insert id="insert" parameterType="${package.Entity}.${entity}">
        insert into ${table.name} (
        <#list table.fields as field>
            <if test="${table.name}.${field.propertyName} != null and ${table.name}.${field.propertyName} !=''">${field.name}<#if field_has_next>,</#if></if>
        </#list>
        )
        values (
        <#list table.fields as field>
            <if test="${field.propertyName} != null and ${table.name}.${field.propertyName} !=''"><#noparse>#{</#noparse>${table.name}.${field.propertyName}<#noparse>}</#noparse><#if field_has_next>,</#if></if>
        </#list>
        )
    </insert>

    <!-- 更新操作 -->
    <update id="update" parameterType="${package.Entity}.${entity}">
        update ${table.name} set id = #{</#noparse>${table.name}.${field.propertyName}<#noparse>}
            <#list table.fields as field>
                <if test="${table.name}.${field.propertyName} != null and ${table.name}.${field.propertyName} !=''">${field.name} = <#noparse>#{</#noparse>${table.name}.${field.propertyName}<#noparse>}</#noparse><#if field_has_next>,</#if></if>
            </#list>
        where id = <#noparse>#{</#noparse>${table.name}.id<#noparse>}</#noparse>
    </update>


</mapper>
