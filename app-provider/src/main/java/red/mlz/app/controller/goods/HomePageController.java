package red.mlz.app.controller.goods;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import red.mlz.app.module.banner.service.BannerService;
import red.mlz.app.module.channel.service.ChannelService;
import red.mlz.app.module.event.service.EventService;
import red.mlz.app.module.goods.service.GoodsService;
import red.mlz.common.module.banner.entity.Banner;
import red.mlz.common.module.channel.entity.Channel;
import red.mlz.common.module.event.entity.Event;

import java.util.List;

@RestController
public class HomePageController {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private BannerService bannerService;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private EventService eventService;

    @RequestMapping("/banner")
    public List<Banner> BannerAll() {
        // 获取Banner数据
        List<Banner> bannerList = bannerService.getAll();
        return bannerList;
    }

    @RequestMapping("/channel")
    public List<Channel> ChannelAll() {
        // 获取Channel数据
        List<Channel> channelList = channelService.getAll();
        return channelList;
    }

    @RequestMapping("/event")
    public List<Event> EventAll() {
        // 获取Event数据
        List<Event> eventList = eventService.getAll();
        return eventList;
    }


}
