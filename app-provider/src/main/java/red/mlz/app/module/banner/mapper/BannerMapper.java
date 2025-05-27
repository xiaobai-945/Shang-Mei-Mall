package red.mlz.app.module.banner.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import red.mlz.common.module.banner.entity.Banner;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface BannerMapper {

// 获取类目列表
@Select("SELECT * FROM banner WHERE is_deleted=0")
List<Banner> getAll();


// 根据ID查询操作
@Select("SELECT * FROM banner WHERE id =  #{id} AND is_deleted=0")
Banner getById(@Param("id")BigInteger id);

// 根据ID提取操作
@Select("SELECT * FROM banner WHERE id =  #{id}")
Banner extractById(@Param("id")BigInteger id);

// 插入操作
int insert(@Param("banner") Banner banner);

// 更新操作
int update(@Param("banner") Banner banner);

// 删除操作
@Update("UPDATE banner SET updated_time = #{time} , is_deleted = 1 WHERE id = #{id}")
int delete(@Param("id")BigInteger id,@Param("time") Integer time );


}