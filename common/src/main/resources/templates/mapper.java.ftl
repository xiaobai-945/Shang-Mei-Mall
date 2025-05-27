<#assign className = table.mapperName>

package ${package.Mapper};

import ${package.Entity}.${entity};
import org.apache.ibatis.annotations.*;
import java.math.BigInteger;

@Mapper
public interface ${className} {

// 获取类目列表
@Select("SELECT * FROM ${table.name} WHERE is_deleted=0")
List<${entity}> getAll();


// 根据ID查询操作
@Select("SELECT * FROM ${table.name} WHERE id =  <#noparse>#{</#noparse>id<#noparse>}</#noparse> AND is_deleted=0")
${entity} getById(@Param("id")BigInteger id);

// 根据ID提取操作
@Select("SELECT * FROM ${table.name} WHERE id =  <#noparse>#{</#noparse>id<#noparse>}</#noparse>")
${entity} extractById(@Param("id")BigInteger id);

// 插入操作
int insert(@Param("category") ${entity} ${table.name});

// 更新操作
int update(@Param("${table.name}") ${entity} ${table.name});

// 删除操作
@Update("UPDATE ${table.name} SET updated_time = <#noparse>#{</#noparse>time<#noparse>}</#noparse> , is_deleted = 1 WHERE id = <#noparse>#{</#noparse>id<#noparse>}</#noparse>")
int delete(@Param("id")BigInteger id,@Param("time") Integer time );


}