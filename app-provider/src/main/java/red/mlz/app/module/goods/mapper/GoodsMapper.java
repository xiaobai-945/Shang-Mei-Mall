package red.mlz.app.module.goods.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import red.mlz.common.module.goods.dto.GoodsDTO;
import red.mlz.common.module.goods.entity.Goods;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface GoodsMapper  {
    @Select("select * from goods WHERE id=#{id} and is_deleted = 0 ")
    Goods getById(@Param("id") BigInteger id);

    @Select("select * from goods WHERE id=#{id}")
    Goods extractById(@Param("id") BigInteger id);

    // 获取列表信息｜｜查询商品
    List<Goods> getAll(@Param("title") String title,@Param("offset") int offset,@Param("limit") int limit,@Param("ids") String ids);

    // 获取列表信息｜｜查询商品（ 连表方式 ）
    List<GoodsDTO> getGoodsDtoAll(@Param("title") String title, @Param("offset") int offset, @Param("limit") int limit);


    // 获取商品总数

    int getGoodsTotalForConsole(@Param("title") String title);

    @Select("select count(*) from goods ")
    int getCount();

    int update(@Param("goods") Goods goods);

    int insert(@Param("goods") Goods goods);

    @Update("update goods set is_deleted = 1,updated_time = #{time} where id=#{id} limit 1")
    int delete(@Param("id") BigInteger id, @Param("time") Integer time);

    @Update("update goods set is_deleted = 1,updated_time = #{time} where category_id=#{category_id} limit 1")
    int deleteCategory(@Param("category_id") BigInteger id, @Param("time") Integer time);

}
