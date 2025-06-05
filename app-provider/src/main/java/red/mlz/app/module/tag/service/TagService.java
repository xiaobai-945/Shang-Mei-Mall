package red.mlz.app.module.tag.service;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;
import red.mlz.app.module.goods_tag_relation.mapper.GoodsTagRelationMapper;
import red.mlz.app.module.tag.mapper.TagMapper;
import red.mlz.common.config.ReadOnly;
import red.mlz.common.module.goods_tag_relation.entity.GoodsTagRelation;
import red.mlz.common.module.tag.entity.Tag;

import red.mlz.common.utils.BaseUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagService {
    @Resource
    private TagMapper mapper;

    @Resource
    private GoodsTagRelationMapper relationMapper;

    @Resource
    private RestHighLevelClient elasticsearchClient;

    @ReadOnly
    public List<Tag> getAll() {
        return mapper.getAll();
    }


    @ReadOnly
    public Tag getById(BigInteger id) {
        return mapper.getById(id);
    }

    @ReadOnly
    public Tag extractById(BigInteger id) {
        return mapper.extractById(id);
    }


    // 获取商品的标签列表
    //@ReadOnly
//    public List<String> getGoodsTags(BigInteger goodsId) {
//        // 获取商品与标签的关联列表
//        List<GoodsTagRelation> goodsTagList = relationMapper.getTagIdByGoodsId(goodsId);
//
//        // 如果没有标签关联，直接返回空列表
//        if (goodsTagList.isEmpty()) {
//            return new ArrayList<>();
//        }
//
//        // 存放标签id打包ids
//        StringBuilder idsStr = new StringBuilder();
//        for (int i = 0; i < goodsTagList.size(); i++) {
//            idsStr.append(goodsTagList.get(i).getTagId().toString());  // 拼接标签ID
//            if (i != goodsTagList.size() - 1) {
//                idsStr.append(",");
//            }
//        }
//        // 获取所有标签（批量查询标签）
//        List<Tag> tags = mapper.getTagByIds(idsStr.toString());
//
//        // 创建标签名称列表并直接从 tags 中获取名称
//        List<String> tagNames = new ArrayList<>();
//        for (GoodsTagRelation relation : goodsTagList) {
//            for (Tag tag : tags) {
//                if (tag.getId().equals(relation.getTagId())) {
//                    tagNames.add(tag.getName());  // 根据关联的 tagId 获取 tag 名称
//                    break;  // 找到对应的标签后退出内层循环
//                }
//            }
//        }
//
//        return tagNames; // 返回标签名称列表
//
//    }

    @ReadOnly
    public List<String> getGoodsTags(BigInteger goodsId) {
        // 查数据库获取商品与标签关系
        List<GoodsTagRelation> relations = relationMapper.getTagIdByGoodsId(goodsId);
        if (relations.isEmpty()) {
            return new ArrayList<>();
        }


        //  拿到所有标签 ID
        List<String> tagIds = relations.stream()
                .map(relation -> relation.getTagId().toString())
                .collect(Collectors.toList());

        // 构建 es 查询
        SearchRequest request = new SearchRequest("tags");  // ES索引名
        TermsQueryBuilder query = new TermsQueryBuilder("id", tagIds);
        SearchSourceBuilder builder = new SearchSourceBuilder()
                .query(query)
                .fetchSource(new String[]{"name"}, null); // 只查name字段
        request.source(builder);

        // 执行查询
        List<String> tagNames = new ArrayList<>();
        try {
            SearchResponse response = elasticsearchClient.search(request, RequestOptions.DEFAULT);
            for (SearchHit hit : response.getHits().getHits()) {
                String name = (String) hit.getSourceAsMap().get("name");
                if (name != null) {
                    tagNames.add(name);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("ES查询失败");
        }

        return tagNames; // 返回标签名称列表
    }

    public Tag getTagByName(String name) { return mapper.getTagByName(name); }

    //创建商品标签
    public Tag insert(String name){
        Tag tag = new Tag();
        tag.setName(name);
        tag.setCreatedTime(BaseUtils.currentSeconds());
        tag.setUpdatedTime(BaseUtils.currentSeconds());
        tag.setIsDeleted(0);
        mapper.insert(tag);
       return tag;


    }



    //更新商品标签
    public int update(BigInteger id,String name){
        Tag tags = new Tag();
        tags.setId(id);
        tags.setName(name);
        int timestamp = (int) (System.currentTimeMillis() / 1000);
        tags.setUpdatedTime(timestamp);
        tags.setIsDeleted(0);
        return mapper.update(tags);

    }

    // 删除商品类目
    public int delete(BigInteger id) { return mapper.delete(id, (int) (System.currentTimeMillis() / 1000)); }


}


