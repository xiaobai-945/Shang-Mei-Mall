package red.mlz.console.module.channel.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import red.mlz.common.module.channel.entity.Channel;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface ChannelMapper {

// 获取类目列表
@Select("SELECT * FROM channel WHERE is_deleted=0")
List<Channel> getAll();


// 根据ID查询操作
@Select("SELECT * FROM channel WHERE id =  #{id} AND is_deleted=0")
Channel getById(@Param("id")BigInteger id);

// 根据ID提取操作
@Select("SELECT * FROM channel WHERE id =  #{id}")
Channel extractById(@Param("id")BigInteger id);

// 插入操作
int insert(@Param("channel") Channel channel);

// 更新操作
int update(@Param("channel") Channel channel);

// 删除操作
@Update("UPDATE channel SET updated_time = #{time} , is_deleted = 1 WHERE id = #{id}")
int delete(@Param("id")BigInteger id,@Param("time") Integer time );


}