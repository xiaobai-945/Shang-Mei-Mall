package red.mlz.app.api.goods;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import red.mlz.common.module.banner.entity.Banner;
import red.mlz.common.module.channel.entity.Channel;
import red.mlz.common.module.event.entity.Event;

import java.util.List;

@FeignClient(name="app-provider")
public interface HomePageFeignClient {

    @RequestMapping("/banner")
    List<Banner> BannerAll();

    @RequestMapping("/channel")
    List<Channel> ChannelAll();

    @RequestMapping("/event")
    List<Event> EventAll();

}
