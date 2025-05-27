package red.mlz.console.module.goods_tag_relation.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import red.mlz.common.module.goods_tag_relation.entity.GoodsTagRelation;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface GoodsTagRelationMapper {

    // 获取标签列表
    @Select("SELECT * FROM goods_tag_relation WHERE is_deleted=0")
    List<GoodsTagRelation> getAll();


    // 根据ID查询操作
    @Select("SELECT * FROM goods_tag_relation WHERE id =  #{id} AND is_deleted=0")
    GoodsTagRelation getById(@Param("id")BigInteger id);

    // 获取商品关联的所有标签ID
    @Select("SELECT tag_id FROM goods_tag_relation WHERE goods_id = #{goodsId} AND is_deleted=0")
    List<GoodsTagRelation> getTagIdByGoodsId(@Param("goodsId") BigInteger goodsId);

    @Select("SELECT tag_id FROM goods_tag_relation WHERE goods_id = #{goodsId} AND is_deleted=0")
    List<BigInteger> getTagIdGoodsId(@Param("goodsId") BigInteger goodsId);
    // 根据ID提取操作
    @Select("SELECT * FROM goods_tag_relation WHERE id =  #{id}")
    GoodsTagRelation extractById(@Param("id")BigInteger id);

    // 插入操作
    int insert(@Param("goodsTagRelation") GoodsTagRelation goodsTagRelation);

    // 更新操作
    int update(@Param("goodsTagRelation") GoodsTagRelation goodsTagRelation);

    // 删除商品与标签的关联关系
    @Update("UPDATE goods_tag_relation SET is_deleted = 1 WHERE goods_id = #{goodsId} AND tag_id = #{tagId} limit 1")
    int deleteRelation(@Param("goodsId") BigInteger goodsId, @Param("tagId") BigInteger tagId);



}