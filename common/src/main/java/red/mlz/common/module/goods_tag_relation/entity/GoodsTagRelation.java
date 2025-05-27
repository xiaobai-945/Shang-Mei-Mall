package red.mlz.common.module.goods_tag_relation.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;


@Data
@Accessors(chain = true)
public class GoodsTagRelation {
    //
    private BigInteger id;
    //商品id
    private BigInteger goodsId;
    //标签id
    private BigInteger tagId;
    //创建时间
    private Integer createdTime;
    //更新时间
    private Integer updatedTime;
    //删除
    private Integer isDeleted;
}