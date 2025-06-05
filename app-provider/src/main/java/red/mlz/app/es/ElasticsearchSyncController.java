package red.mlz.app.es;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import red.mlz.app.module.es.ElasticsearchSyncService;
import red.mlz.common.utils.Response;


import javax.annotation.Resource;

@RestController
public class ElasticsearchSyncController {

    @Resource
    private ElasticsearchSyncService elasticsearchSyncService;

    /**
     * 手动同步所有商品数据到ES
     */
    @RequestMapping("es/sync/all")
    public Response syncAllGoods() {
        try {
            elasticsearchSyncService.syncAllGoodsToES();
            return new Response<>(1001, "数据同步成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>(4004, "数据同步失败: " + e.getMessage());
        }
    }

    /**
     * 初始化ES索引
     */
    @RequestMapping("es/init")
    public Response initIndex() {
        try {
            elasticsearchSyncService.initializeIndex();
            return new Response<>(1001, "索引初始化成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>(4004, "索引初始化失败: " + e.getMessage());
        }
    }

    /**
     * 检查ES连接状态
     */
    @RequestMapping("es/status")
    public Response checkStatus() {
        try {
            boolean isAvailable = elasticsearchSyncService.isElasticsearchAvailable();
            if (isAvailable) {
                return new Response<>(1001, "ES服务正常");
            } else {
                return new Response<>(4004, "ES服务不可用");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>(4004, "检查ES状态失败: " + e.getMessage());
        }
    }
} 