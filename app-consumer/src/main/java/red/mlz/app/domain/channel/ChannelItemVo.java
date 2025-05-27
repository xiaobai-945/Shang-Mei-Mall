package red.mlz.app.domain.channel;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;

@Data
@Accessors(chain = true)
public class ChannelItemVo {
    //
    private BigInteger id;
    //频道内容
    private String content;
    //创建时间
    private Integer createdTime;
    //更新时间
    private Integer updatedTime;
    //删除
    private BigInteger isDeleted;
}
