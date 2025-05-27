package red.mlz.common.module.event.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;


@Data
@Accessors(chain = true)
public class Event {
    //
    private BigInteger id;
    //活动内容
    private String content;
    //创建时间
    private Integer createdTime;
    //更新时间
    private Integer updatedTime;
    //删除
    private Integer isDeleted;
}