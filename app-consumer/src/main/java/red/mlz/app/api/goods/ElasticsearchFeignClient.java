package red.mlz.app.api.goods;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;


@FeignClient(name="app-provider")
public interface ElasticsearchFeignClient {

    /**
     * 手动同步所有商品数据到ES
     */
    @RequestMapping("es/sync/all")
    void syncAllGoods();

    /**
     * 初始化ES索引
     */
    @RequestMapping("es/init")
    void initIndex();

    /**
     * 检查ES连接状态
     */
    @RequestMapping("es/status")
    boolean checkStatus();


}