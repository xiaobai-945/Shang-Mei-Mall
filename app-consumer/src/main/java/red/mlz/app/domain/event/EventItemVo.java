package red.mlz.app.domain.event;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;

@Data
@Accessors(chain = true)
public class EventItemVo {
    private BigInteger id;
    //活动
    private String content;
    //创建时间
    private Integer createdTime;
    //更新时间
    private Integer updatedTime;
    //删除
    private BigInteger isDeleted;
}
