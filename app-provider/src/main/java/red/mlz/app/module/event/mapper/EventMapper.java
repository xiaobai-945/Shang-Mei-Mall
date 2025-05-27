package red.mlz.app.module.event.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import red.mlz.common.module.event.entity.Event;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface EventMapper {

// 获取类目列表
@Select("SELECT * FROM event WHERE is_deleted=0")
List<Event> getAll();


// 根据ID查询操作
@Select("SELECT * FROM event WHERE id =  #{id} AND is_deleted=0")
Event getById(@Param("id")BigInteger id);

// 根据ID提取操作
@Select("SELECT * FROM event WHERE id =  #{id}")
Event extractById(@Param("id")BigInteger id);

// 插入操作
int insert(@Param("event") Event event);

// 更新操作
int update(@Param("event") Event event);

// 删除操作
@Update("UPDATE event SET updated_time = #{time} , is_deleted = 1 WHERE id = #{id}")
int delete(@Param("id")BigInteger id,@Param("time") Integer time );


}