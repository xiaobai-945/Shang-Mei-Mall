package red.mlz.common.module.banner.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;


@Data
@Accessors(chain = true)
public class Banner {
    //
    private BigInteger id;
    //广告图
    private String image;
    //创建时间
    private Integer createdTime;
    //更新时间
    private Integer updatedTime;
    //删除
    private Integer isDeleted;
}