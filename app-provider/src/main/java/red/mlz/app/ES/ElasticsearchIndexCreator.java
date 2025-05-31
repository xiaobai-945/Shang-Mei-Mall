package red.mlz.module.ES;

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
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("47.113.121.13", 9200, "http"))
        );

        try {
            // 创建 goods_index 索引
            CreateIndexRequest goodsIndexRequest = new CreateIndexRequest("goods_index");
            String goodsMapping = "{\n" +
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

            goodsIndexRequest.mapping(goodsMapping, XContentType.JSON);
            CreateIndexResponse goodsIndexResponse = client.indices().create(goodsIndexRequest, RequestOptions.DEFAULT);

            if (goodsIndexResponse.isAcknowledged()) {
                System.out.println("goods_index created successfully!");
            } else {
                System.out.println("Failed to create goods_index.");
            }

            // 创建 category_index 索引
            CreateIndexRequest categoryIndexRequest = new CreateIndexRequest("category_index");
            String categoryMapping = "{\n" +
                    "  \"mappings\": {\n" +
                    "    \"properties\": {\n" +
                    "      \"id\": {\"type\": \"long\"},\n" +
                    "      \"parentId\": {\"type\": \"long\"},\n" +
                    "      \"name\": {\"type\": \"text\", \"analyzer\": \"standard\"},\n" +
                    "      \"image\": {\"type\": \"keyword\"}\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";

            categoryIndexRequest.mapping(categoryMapping, XContentType.JSON);
            CreateIndexResponse categoryIndexResponse = client.indices().create(categoryIndexRequest, RequestOptions.DEFAULT);

            if (categoryIndexResponse.isAcknowledged()) {
                System.out.println("category_index created successfully!");
            } else {
                System.out.println("Failed to create category_index.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
