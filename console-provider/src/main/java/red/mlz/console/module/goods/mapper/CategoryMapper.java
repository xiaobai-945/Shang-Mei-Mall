
package red.mlz.console.module.goods.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import red.mlz.common.module.goods.entity.Category;
import red.mlz.common.module.goods.entity.Goods;

import java.math.BigInteger;
import java.util.List;


@Mapper
public interface CategoryMapper {

// 获取类目列表
@Select("SELECT * FROM category WHERE is_deleted=0")
List<Category> getAll();

// 关于类目的模糊查询
@Select("select id from category where name LIKE CONCAT('%',#{title},'%')")
List<BigInteger> selectIdByTitle(@Param("title") String title);


// 根据ID查询操作
@Select("SELECT * FROM category WHERE id = #{id} AND is_deleted=0")
Category getById(@Param("id") BigInteger id);

// 根据ID查询数据
@Select("SELECT * FROM category WHERE id in (${ids} ) AND is_deleted = 0")
List<Category> getByIds(@Param("ids") String ids);

// 根据ID提取操作
@Select("SELECT * FROM category WHERE id = #{id}")
Category extractById(@Param("id")BigInteger id);

// 获取 parentId 为null的父类目
@Select("SELECT * FROM category WHERE parent_id IS null ")
List<Category> getByParentAll();

// 获取父类目下子类目数据
@Select("SELECT * FROM category WHERE parent_id = #{parentId}")
List<Category> getCategoryAll(@Param("parentId") BigInteger parentId);


// 子类目类列表
@Select("SELECT * FROM category WHERE parent_id IS NOT NULL")
List<Category> getCategories();

// 类目下商品列表

@Select("SELECT * FROM goods WHERE category_id = #{categoryId} LIMIT #{limit} OFFSET #{offset}")
List<Goods> getGoodsByCategoryId(@Param("categoryId") BigInteger categoryId, @Param("offset") int offset, @Param("limit") int limit);

// 获取商品总数
@Select("select count(*) from goods WHERE category_id = #{categoryId}")
Long getCategoryGoodsCount(@Param("categoryId") BigInteger categoryId);

// 插入操作
int insert(@Param("category") Category category);

// 更新操作
int update(@Param("category") Category category);

// 删除操作
@Update("UPDATE category SET updated_time = #{time} , is_deleted = 1 WHERE id = #{id}")
int delete(@Param("id")BigInteger id,@Param("time") Integer time );


}