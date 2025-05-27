package red.mlz.app.controller.goods;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import red.mlz.app.api.goods.GoodsFeignClient;
import red.mlz.app.api.goods.HomePageFeignClient;

import red.mlz.app.domain.BaseListVo;
import red.mlz.app.domain.HomePageVo;
import red.mlz.app.domain.banner.BannerItemVo;
import red.mlz.app.domain.banner.BannerVo;
import red.mlz.app.domain.channel.ChannelItemVo;
import red.mlz.app.domain.channel.ChannelVo;
import red.mlz.app.domain.event.EventItemVo;
import red.mlz.app.domain.event.EventVo;
import red.mlz.app.domain.goods.GoodsListVo;
import red.mlz.common.module.banner.entity.Banner;
import red.mlz.common.module.channel.entity.Channel;
import red.mlz.common.module.event.entity.Event;
import red.mlz.common.module.goods.entity.Goods;
import red.mlz.common.utils.ImageInfo;
import red.mlz.common.utils.ImageUtils;
import red.mlz.common.utils.Response;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class HomePageController {
    @Resource
    private HomePageFeignClient homePageFeignClient;

    @Resource
    private GoodsFeignClient goodsFeignClient;

    @RequestMapping("/home")
    public Response HomePage(){

        ExecutorService executor = Executors.newFixedThreadPool(4);

        CountDownLatch countDownLatch = new CountDownLatch(4);

        // 存储各个线程的结果，将它们放入HomePageVo
        BannerVo bannerVo = new BannerVo();
        ChannelVo channelVo = new ChannelVo();
        EventVo eventVo = new EventVo();
        BaseListVo result = new BaseListVo();

        // 获取Banner数据
        executor.submit(() -> {
            List<Banner> bannerList = homePageFeignClient.BannerAll();
            List<BannerItemVo> bannerVoList = new ArrayList<>();
            for (Banner banner : bannerList) {
                BannerItemVo bannerItemVo = new BannerItemVo();
                bannerItemVo.setId(banner.getId());
                bannerItemVo.setImage(banner.getImage());
                bannerItemVo.setCreatedTime(banner.getCreatedTime());
                bannerVoList.add(bannerItemVo);
            }
            bannerVo.setBannerList(bannerVoList);
            countDownLatch.countDown();
        });
        // 获取Channel数据
        executor.submit(() -> {
            List<Channel> channelList = homePageFeignClient.ChannelAll();
            List<ChannelItemVo> channelVoList = new ArrayList<>();
            for (Channel channel : channelList) {
                ChannelItemVo channelItemVo = new ChannelItemVo();
                channelItemVo.setId(channel.getId());
                channelItemVo.setCreatedTime(channel.getCreatedTime());
                channelItemVo.setContent(channel.getContent());
                channelVoList.add(channelItemVo);
            }
            channelVo.setChannelItemVos(channelVoList);
            countDownLatch.countDown();
        });
        // 获取Event数据
        executor.submit(() -> {
            List<Event> eventList = homePageFeignClient.EventAll();
            List<EventItemVo> eventVoList = new ArrayList<>();
            for (Event event : eventList) {
                EventItemVo eventItemVo = new EventItemVo();
                eventItemVo.setId(eventItemVo.getId());
                eventItemVo.setContent(event.getContent());
                eventItemVo.setCreatedTime(event.getCreatedTime());
                eventVoList.add(eventItemVo);
            }
            eventVo.setEventListVo(eventVoList);
            countDownLatch.countDown();
        });

        // 获取第一页商品数据作为推荐内容
        executor.submit(() -> {
            List<Goods> goodsList = goodsFeignClient.getGoodsAll(null, 1, 10);
            List<GoodsListVo> goodsListVoList = new ArrayList<>();
            for (Goods goods : goodsList) {
                GoodsListVo goodsListVo = new GoodsListVo();
                goodsListVo.setId(goods.getId());
                // 将轮播图图片用 “ $ ” 连接
                String[] images = goods.getGoodsImages().split("\\$");
                // 获取图片信息，包含 AR 和 URL
                ImageInfo imageInfo = ImageUtils.getImageInfo(images[0]);
                goodsListVo.setGoodsImage(imageInfo);
                goodsListVo.setTitle(goods.getGoodsName());
                goodsListVo.setSales(goods.getSales());
                goodsListVo.setPrice(goods.getPrice());
                goodsListVoList.add(goodsListVo);
            }
            result.setList(goodsListVoList);
            countDownLatch.countDown();
        });

        try {
            countDownLatch.await();
        } catch (Exception e) {
            return new Response<>(4004);
        }
        HomePageVo homePageVo = new HomePageVo();
        homePageVo.setBanner(bannerVo);
        homePageVo.setChannel(channelVo);
        homePageVo.setEvent(eventVo);
        homePageVo.setGoodsList(result);
        return new Response<>(1001, homePageVo);
    }


}
