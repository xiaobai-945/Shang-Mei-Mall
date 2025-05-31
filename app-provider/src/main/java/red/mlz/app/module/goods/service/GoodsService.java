package red.mlz.app.module.goods.service;


import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import red.mlz.app.module.goods.mapper.GoodsMapper;
import red.mlz.app.module.goods_tag_relation.service.GoodsTagRelationService;
import red.mlz.app.module.tag.service.TagService;
import red.mlz.common.config.ReadOnly;
import red.mlz.common.module.goods.dto.GoodsDTO;
import red.mlz.common.module.goods.entity.Category;
import red.mlz.common.module.goods.entity.Goods;
import red.mlz.common.module.goods.request.GoodsContentDto;
import red.mlz.common.module.tag.entity.Tag;

import red.mlz.common.utils.BaseUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {
    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private CategoryService categoryService;
    @Resource
    private TagService tagService;
    @Resource
    private GoodsTagRelationService relationService;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Resource
    private RestHighLevelClient client;

    // 商品详情
    @Transactional
    @ReadOnly
    public Goods getById(BigInteger id) {
        return goodsMapper.getById(id);
    }

    // 获取一条商品数据
    @ReadOnly
    public Goods extractById(BigInteger id) {
        return goodsMapper.extractById(id);
    }


    @ReadOnly
    public List<Goods> getAllGoodsInfo(String title, int page, int pageSize) {
        if (BaseUtils.isEmpty(title)) {
            // 如果搜索关键词为空，返回空或者全量可以根据业务调整，这里先返回空
            return Collections.emptyList();
        }

        // 1. 先用 ES 分类索引匹配分类名，获取匹配的分类ID列表
        List<BigInteger> categoryIds = new ArrayList<>();
        try {
            SearchRequest categorySearchRequest = new SearchRequest("category_index");
            SearchSourceBuilder categorySourceBuilder = new SearchSourceBuilder();

            categorySourceBuilder.query(QueryBuilders.matchQuery("name", title));

            categorySourceBuilder.size(100);  // 最大返回数量，可根据业务调整

            categorySearchRequest.source(categorySourceBuilder);

            SearchResponse categoryResponse = client.search(categorySearchRequest, RequestOptions.DEFAULT);

            for (SearchHit hit : categoryResponse.getHits()) {
                Object idObj = hit.getSourceAsMap().get("id");
                if (idObj != null) {
                    if (idObj instanceof Number) {
                        categoryIds.add(BigInteger.valueOf(((Number) idObj).longValue()));
                    } else {
                        categoryIds.add(new BigInteger(idObj.toString()));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }

        // 2. 如果没有找到匹配的分类，直接返回空
        if (categoryIds.isEmpty()) {
            return Collections.emptyList();
        }
        // 3. 计算分页偏移量
        int offset = (page - 1) * pageSize;

        // 4. 构建商品索引的搜索请求
        List<Goods> goodsList = new ArrayList<>();
        try {
            SearchRequest goodsSearchRequest = new SearchRequest("goods_index");
            SearchSourceBuilder goodsSourceBuilder = new SearchSourceBuilder();

            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            boolQuery.must(QueryBuilders.termsQuery("categoryId", categoryIds)); // 分类ID过滤

            // 关键词匹配商品标题或分类名，至少匹配一个
            boolQuery.should(QueryBuilders.matchQuery("title", title));
            boolQuery.should(QueryBuilders.matchQuery("categoryName", title));
            boolQuery.minimumShouldMatch(1);

            goodsSourceBuilder.query(boolQuery);
            goodsSourceBuilder.from(offset);
            goodsSourceBuilder.size(pageSize);

            goodsSearchRequest.source(goodsSourceBuilder);

            SearchResponse goodsResponse = client.search(goodsSearchRequest, RequestOptions.DEFAULT);

            ObjectMapper mapper = new ObjectMapper();
            for (SearchHit hit : goodsResponse.getHits()) {
                Goods goods = mapper.convertValue(hit.getSourceAsMap(), Goods.class);
                goodsList.add(goods);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }

        return goodsList;
    }
    // 商品列表
//    @ReadOnly
//    public List<Goods> getAllGoodsInfo(String title, int page, int pageSize) {
//        // 获取符合类目的 category_id 列表
//        List<BigInteger> categoryIds = categoryService.selectIdByTitle(title);
//
//        // StringBuilder 拼接 ID 列表
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < categoryIds.size(); i++) {
//            sb.append(categoryIds.get(i));
//            if (i < categoryIds.size() - 1) {
//                sb.append(",");  // 逗号分隔
//            }
//        }
//        // 获取拼接字符串-ids
//        String ids = sb.toString();
//
//        // System.out.println(ids);
//
//        int offset = (page - 1) * pageSize;
//
//        return goodsMapper.getAll(title, offset, pageSize, ids);
//    }


    // 商品列表(连表方式）
    @ReadOnly
    public List<GoodsDTO> getAllGoods(String title, int page, int pageSize) {


        int offset = (page - 1) * pageSize;

        return goodsMapper.getGoodsDtoAll(title, offset, pageSize);
    }


    // 商品数量
    @ReadOnly
    public int getGoodsTotalForConsole(String title) {
        return goodsMapper.getGoodsTotalForConsole(title);
    }


    // 新增修改

    public BigInteger edit(BigInteger id, BigInteger categoryId, String title, String goodsImages, Integer sales,
                           String goodsName, Integer price, String source,
                           Integer sevenDayReturn,String content,String tagNames) {

        // 开始手动管理事务
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());


        try {
            List<GoodsContentDto> checkContents = JSON.parseArray(content, GoodsContentDto.class);
            for (GoodsContentDto checkContent : checkContents) {
                if (!GoodsDefine.isArticleContentType(checkContent.getType())) {
                    throw new RuntimeException("goods content is error");
                }
            }
        } catch (Exception cause) {
            // 如果处理 content 出现错误，回滚事务
            transactionManager.rollback(status);
            throw new RuntimeException("goods content is error");
        }
        if (BaseUtils.isEmpty(title) || BaseUtils.isEmpty(goodsImages)) {
            transactionManager.rollback(status);
            throw new RuntimeException("goods title or goodsImages is error");
        }


        if (sales == null || sales < 0) {
            transactionManager.rollback(status);
            throw new IllegalArgumentException("销量不能为负数");
        }
        if (goodsName == null || goodsName.trim().isEmpty()) {
            transactionManager.rollback(status);
            throw new RuntimeException("商品名称不能为空");
        }
        if (price == null || price < 0) {
            transactionManager.rollback(status);
            throw new IllegalArgumentException("价格不能为负数");
        }
        if (source == null || source.trim().isEmpty()) {
            transactionManager.rollback(status);
            throw new RuntimeException("来源不能为空");
        }
        if (sevenDayReturn == null || (sevenDayReturn != 0 && sevenDayReturn != 1)) {
            transactionManager.rollback(status);
            throw new IllegalArgumentException("七天退货字段取值只能为0或1");
        }

        // 校验类目id是否存在
        Category existCategoryId = categoryService.getById(categoryId);
        if (existCategoryId == null) {
            transactionManager.rollback(status);
            throw new IllegalArgumentException("类目id不存在");
        }

        List<BigInteger> tagIds = new ArrayList<>();

        // 解析标签
        String[] tags = tagNames.split("\\$");

        for (String tagName : tags) {
            Tag tag = tagService.getTagByName(tagName);
            if (tag != null) {
                tagIds.add(tag.getId());
            } else {
                Tag newTag = tagService.insert(tagName);
                if (newTag != null) {
                    // 新插入标签成功，添加其ID到tagIds
                    tagIds.add(newTag.getId());
                } else {
                    // 插入失败，回滚事务
                    transactionManager.rollback(status);
                    throw new RuntimeException("插入新标签失败");
                }
            }
        }

        Goods goods = new Goods();
        goods.setCategoryId(categoryId);
        goods.setTitle(title);
        goods.setGoodsImages(goodsImages);
        goods.setSales(sales);
        goods.setGoodsName(goodsName);
        goods.setPrice(price);
        goods.setSource(source);
        goods.setSevenDayReturn(sevenDayReturn);
        goods.setGoodsDetails(content);


        goods.setUpdatedTime(BaseUtils.currentSeconds());

        try {
            // 更新逻辑
            if (id != null) {
                // 判断id是否存在
                Goods existId = goodsMapper.getById(id);
                if (existId == null) {
                    transactionManager.rollback(status);
                    throw new RuntimeException("Id不存在，更新失败。");
                }
                goods.setId(id);
                goodsMapper.update(goods);

                // 获取商品当前已关联的标签ID列表
                List<BigInteger> existingTagIds = relationService.getByGoodsId(id);

                // 删除现有的标签关系中不再存在的标签
                for (BigInteger existingTagId : existingTagIds) {
                    if (!tagIds.contains(existingTagId)) {
                        // 如果当前标签ID不在新的标签ID列表中，删除该关系
                        relationService.deleteRelation(id, existingTagId);
                    }
                }

                // 建立新的标签关系
                for (BigInteger tagId : tagIds) {
                    if (!existingTagIds.contains(tagId)) {
                        // 如果该标签ID之前没有和商品建立关系，插入新的关系
                        relationService.insert(id, tagId);
                    }
                }
                transactionManager.commit(status);

                return id;

            } else {
                // 新增逻辑
                goods.setCreatedTime(BaseUtils.currentSeconds());
                goods.setIsDeleted(0);
                goodsMapper.insert(goods);
                BigInteger goodsId = goods.getId();  // 获取新插入商品的 ID

                if (goodsId == null) {
                    transactionManager.rollback(status);
                    throw new RuntimeException("商品插入失败，未生成ID");
                }

                for (BigInteger tagId : tagIds) {
                    // 如果该标签ID之前没有和商品建立关系，插入新的关系
                    relationService.insert(goodsId, tagId);
                }
                // 提交事务
                transactionManager.commit(status);
                return goods.getId();
            }
        }catch(Exception e){
                // 如果任何操作出错，回滚事务
                transactionManager.rollback(status);
                throw new RuntimeException("事务执行失败，已回滚", e);
            }
        }

        // 删除商品
        public int deleteGoods (BigInteger id){
            return goodsMapper.delete(id, (int) (System.currentTimeMillis() / 1000));
        }

        // 商品类目里的所有商品
        public int deleteCategory (BigInteger id){
            return goodsMapper.deleteCategory(id, (int) (System.currentTimeMillis() / 1000));
        }
    }