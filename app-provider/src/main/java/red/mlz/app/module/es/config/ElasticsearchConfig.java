package red.mlz.app.module.es.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        RestClientBuilder builder = RestClient.builder(
            new HttpHost("47.113.121.13", 9200, "http")
        );
        
        // 设置请求超时时间和连接超时时间
        builder.setRequestConfigCallback(requestConfigBuilder -> 
            requestConfigBuilder
                .setConnectTimeout(5000)  // 连接超时5秒
                .setSocketTimeout(60000)  // Socket超时60秒
                .setConnectionRequestTimeout(1000) // 获取连接超时1秒
        );
        
        // 设置HTTP客户端配置
        builder.setHttpClientConfigCallback(httpClientBuilder -> 
            httpClientBuilder
                .setMaxConnTotal(100)  // 最大连接数100
                .setMaxConnPerRoute(50) // 每个路由最大连接数50
        );
        
        return new RestHighLevelClient(builder);
    }
} 