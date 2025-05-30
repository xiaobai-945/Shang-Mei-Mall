package red.mlz.app.ES;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.xcontent.XContentType;

import java.io.IOException;

public class ElasticsearchIndexCreator {

    public static void main(String[] args) {
        // 创建 RestHighLevelClient 客户端

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))  // 配置 Elasticsearch 服务地址
        );

        try {
            // 创建索引请求
            CreateIndexRequest request = new CreateIndexRequest("goods_index");

            // 定义映射
            String mapping = "{\n" +
                    "  \"mappings\": {\n" +
                    "    \"properties\": {\n" +
                    "      \"id\": {\"type\": \"long\"},\n" +
                    "      \"categoryId\": {\"type\": \"long\"},\n" +
                    "      \"title\": {\"type\": \"text\"},\n" +
                    "      \"goodsImages\": {\"type\": \"text\"},\n" +
                    "      \"sales\": {\"type\": \"integer\"},\n" +
                    "      \"goodsName\": {\"type\": \"text\"},\n" +
                    "      \"price\": {\"type\": \"integer\"},\n" +
                    "      \"source\": {\"type\": \"text\"},\n" +
                    "      \"sevenDayReturn\": {\"type\": \"integer\"},\n" +
                    "      \"goodsDetails\": {\"type\": \"text\"},\n" +
                    "      \"createdTime\": {\"type\": \"integer\"},\n" +
                    "      \"updatedTime\": {\"type\": \"integer\"},\n" +
                    "      \"isDeleted\": {\"type\": \"integer\"}\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";

            // 设置映射
            request.mapping(mapping, XContentType.JSON);

            // 执行创建索引请求
            CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);

            // 检查是否成功创建索引
            if (createIndexResponse.isAcknowledged()) {
                System.out.println("Index created successfully!");
            } else {
                System.out.println("Failed to create index.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭客户端
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
