package red.mlz.app.domain.channel;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ChannelVo {
    private List<ChannelItemVo> channelItemVos;
}
