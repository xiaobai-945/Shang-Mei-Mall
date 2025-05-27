
package red.mlz.app.module.tag.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import red.mlz.common.module.tag.entity.Tag;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface TagMapper {

    // 获取标签列表
    @Select("SELECT * FROM tag WHERE is_deleted=0")
    List<Tag> getAll();

    // 根据ID查询操作
    @Select("SELECT * FROM tag WHERE id =  #{id} AND is_deleted=0")
    Tag getById(@Param("id")BigInteger id);

    // 查询标签
    @Select("SELECT * FROM tag WHERE name = #{name} AND is_deleted = 0")
    Tag getTagByName(String name);

    @Select("SELECT * FROM tag WHERE id in (${ids}) AND is_deleted = 0")
    List<Tag> getTagByIds(@Param("ids") String ids);

    // 根据ID提取操作
    @Select("SELECT * FROM tag WHERE id =  #{id}")
    Tag extractById(@Param("id")BigInteger id);


    // 插入操作
    int insert(@Param("tag") Tag tags);

    // 更新操作
    int update(@Param("tag") Tag tags);

    // 删除操作
    @Update("UPDATE tag SET updated_time = #{time} , is_deleted = 1 WHERE id = #{id}")
    int delete(@Param("id")BigInteger id,@Param("time") Integer time );


}