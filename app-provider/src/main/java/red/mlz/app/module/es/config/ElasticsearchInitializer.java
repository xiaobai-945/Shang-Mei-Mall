package red.mlz.app.module.es.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import red.mlz.app.module.es.ElasticsearchSyncService;

import javax.annotation.Resource;

@Configuration
public class ElasticsearchInitializer {

    @Resource
    private ElasticsearchSyncService elasticsearchSyncService;

    @Bean
    public ApplicationRunner initializeElasticsearch() {
        return args -> {
            try {
                // 检查ES是否可用
                if (elasticsearchSyncService.isElasticsearchAvailable()) {
                    // 应用启动时自动初始化ES索引
                    elasticsearchSyncService.initializeIndex();
                    System.out.println("ES索引初始化完成");
                } else {
                    System.err.println("ES服务不可用，跳过索引初始化");
                }
            } catch (Exception e) {
                System.err.println("ES索引初始化失败: " + e.getMessage());
                // 不抛出异常，避免影响应用启动
            }
        };
    }
} 