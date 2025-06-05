package red.mlz.app.module.es;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.stereotype.Service;
import red.mlz.app.module.goods.mapper.GoodsMapper;
import red.mlz.app.module.goods.service.CategoryService;
import red.mlz.common.module.goods.dto.GoodsSearchDoc;
import red.mlz.common.module.goods.entity.Category;
import red.mlz.common.module.goods.entity.Goods;


import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ElasticsearchSyncService {
    
    @Resource
    private RestHighLevelClient client;
    
    @Resource
    private GoodsMapper goodsMapper;
    
    @Resource
    private CategoryService categoryService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private static final String GOODS_INDEX = "goods_search_index";
    
    /**
     * 检查ES连接状态
     */
    public boolean isElasticsearchAvailable() {
        try {
            return client.ping(RequestOptions.DEFAULT);
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * 初始化ES索引
     */
    public void initializeIndex() throws IOException {
        // 检查索引是否存在
        GetIndexRequest getIndexRequest = new GetIndexRequest(GOODS_INDEX);
        boolean exists = client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        
        if (!exists) {
            // 创建索引 (es 7.x方式)
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(GOODS_INDEX);
            String mapping = getGoodsIndexMapping();
            createIndexRequest.mapping(mapping, XContentType.JSON);
            
            client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            System.out.println("创建索引成功: " + GOODS_INDEX);
        }
    }
    
    /**
     * 获取商品索引映射 (es 7.x格式)
     */
    private String getGoodsIndexMapping() {
        return "{\n" +
                "  \"properties\": {\n" +
                "    \"id\": {\"type\": \"long\"},\n" +
                "    \"categoryId\": {\"type\": \"long\"},\n" +
                "    \"title\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"standard\",\n" +
                "      \"search_analyzer\": \"standard\"\n" +
                "    },\n" +
                "    \"goodsImages\": {\"type\": \"text\"},\n" +
                "    \"sales\": {\"type\": \"integer\"},\n" +
                "    \"goodsName\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"standard\",\n" +
                "      \"search_analyzer\": \"standard\"\n" +
                "    },\n" +
                "    \"price\": {\"type\": \"integer\"},\n" +
                "    \"source\": {\"type\": \"text\"},\n" +
                "    \"sevenDayReturn\": {\"type\": \"integer\"},\n" +
                "    \"goodsDetails\": {\"type\": \"text\"},\n" +
                "    \"createdTime\": {\"type\": \"integer\"},\n" +
                "    \"updatedTime\": {\"type\": \"integer\"},\n" +
                "    \"isDeleted\": {\"type\": \"integer\"},\n" +
                "    \"categoryName\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"standard\",\n" +
                "      \"search_analyzer\": \"standard\"\n" +
                "    },\n" +
                "    \"categoryImage\": {\"type\": \"keyword\"},\n" +
                "    \"parentCategoryId\": {\"type\": \"long\"}\n" +
                "  }\n" +
                "}";
    }
    
    /**
     * 同步所有商品数据到ES
     */
    public void syncAllGoodsToES()  {
        // 初始化索引
        try {
            initializeIndex();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 获取所有商品数据
        List<Goods> allGoods = goodsMapper.getAllForES();
        
        // 获取所有分类数据
        List<Category> categories = categoryService.getAll();
        Map<BigInteger, Category> categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, category -> category));
        
        // 构建批量请求
        BulkRequest bulkRequest = new BulkRequest();
        
        for (Goods goods : allGoods) {
            if (goods.getIsDeleted() == 1) {
                continue; // 跳过已删除的商品
            }
            
            // 获取分类信息
            Category category = categoryMap.get(goods.getCategoryId());
            
            // 构建搜索文档
            GoodsSearchDoc searchDoc = new GoodsSearchDoc()
                    .setId(goods.getId())
                    .setCategoryId(goods.getCategoryId())
                    .setTitle(goods.getTitle())
                    .setGoodsImages(goods.getGoodsImages())
                    .setSales(goods.getSales())
                    .setGoodsName(goods.getGoodsName())
                    .setPrice(goods.getPrice())
                    .setSource(goods.getSource())
                    .setSevenDayReturn(goods.getSevenDayReturn())
                    .setGoodsDetails(goods.getGoodsDetails())
                    .setCreatedTime(goods.getCreatedTime())
                    .setUpdatedTime(goods.getUpdatedTime())
                    .setIsDeleted(goods.getIsDeleted());
            
            // 设置分类信息
            if (category != null) {
                searchDoc.setCategoryName(category.getName())
                        .setCategoryImage(category.getImage())
                        .setParentCategoryId(category.getParentId());
            } else {
                searchDoc.setCategoryName("未分类")
                        .setCategoryImage("")
                        .setParentCategoryId(BigInteger.valueOf(0));
            }
            
            // 转换为Map
            Map<String, Object> docMap = objectMapper.convertValue(searchDoc, Map.class);
            
            // 创建索引请求
            IndexRequest indexRequest = new IndexRequest(GOODS_INDEX)
                    .id(goods.getId().toString())
                    .source(docMap);
            
            bulkRequest.add(indexRequest);
        }
        
        // 执行批量索引
        if (bulkRequest.numberOfActions() > 0) {
            BulkResponse bulkResponse = null;
            try {
                bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (bulkResponse.hasFailures()) {
                System.err.println("批量索引有失败项: " + bulkResponse.buildFailureMessage());
            } else {
                System.out.println("成功同步 " + bulkRequest.numberOfActions() + " 条商品数据到ES");
            }
        }
    }
    
    /**
     * 同步单个商品到ES
     */
    public void syncSingleGoodsToES(BigInteger goodsId) throws IOException {
        // 获取商品信息
        Goods goods = goodsMapper.getById(goodsId);
        if (goods == null || goods.getIsDeleted() == 1) {
            return;
        }
        
        // 获取分类信息
        Category category = categoryService.getById(goods.getCategoryId());
        
        // 构建搜索文档
        GoodsSearchDoc searchDoc = new GoodsSearchDoc()
                .setId(goods.getId())
                .setCategoryId(goods.getCategoryId())
                .setTitle(goods.getTitle())
                .setGoodsImages(goods.getGoodsImages())
                .setSales(goods.getSales())
                .setGoodsName(goods.getGoodsName())
                .setPrice(goods.getPrice())
                .setSource(goods.getSource())
                .setSevenDayReturn(goods.getSevenDayReturn())
                .setGoodsDetails(goods.getGoodsDetails())
                .setCreatedTime(goods.getCreatedTime())
                .setUpdatedTime(goods.getUpdatedTime())
                .setIsDeleted(goods.getIsDeleted());
        
        // 设置分类信息
        if (category != null) {
            searchDoc.setCategoryName(category.getName())
                    .setCategoryImage(category.getImage())
                    .setParentCategoryId(category.getParentId());
        } else {
            searchDoc.setCategoryName("未分类")
                    .setCategoryImage("")
                    .setParentCategoryId(BigInteger.valueOf(0));
        }
        
        // 转换为Map
        Map<String, Object> docMap = objectMapper.convertValue(searchDoc, Map.class);
        
        // 创建索引请求
        IndexRequest indexRequest = new IndexRequest(GOODS_INDEX)
                .id(goods.getId().toString())
                .source(docMap);
        
        // 执行索引
        client.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println("成功同步商品到ES: " + goodsId);
    }
    

} 