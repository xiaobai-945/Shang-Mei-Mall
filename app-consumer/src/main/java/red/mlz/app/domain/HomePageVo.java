package red.mlz.app.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import red.mlz.app.domain.banner.BannerVo;
import red.mlz.app.domain.channel.ChannelVo;
import red.mlz.app.domain.event.EventVo;


@Data
@Accessors(chain = true)
public class HomePageVo {
    private BannerVo banner;
    private ChannelVo channel;
    private EventVo event;
    private BaseListVo goodsList;

}
