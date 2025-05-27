package red.mlz.console.module.tag.service;

import org.springframework.stereotype.Service;
import red.mlz.common.config.ReadOnly;
import red.mlz.common.module.goods_tag_relation.entity.GoodsTagRelation;
import red.mlz.common.module.tag.entity.Tag;

import red.mlz.common.utils.BaseUtils;
import red.mlz.console.module.goods_tag_relation.mapper.GoodsTagRelationMapper;
import red.mlz.console.module.tag.mapper.TagMapper;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class TagService {
    @Resource
    private TagMapper mapper;

    @Resource
    private GoodsTagRelationMapper relationMapper;

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
    @ReadOnly
    public List<String> getGoodsTags(BigInteger goodsId) {
        // 获取商品与标签的关联列表
        List<GoodsTagRelation> goodsTagList = relationMapper.getTagIdByGoodsId(goodsId);

        // 如果没有标签关联，直接返回空列表
        if (goodsTagList.isEmpty()) {
            return new ArrayList<>();
        }

        // 存放标签id打包ids
        StringBuilder idsStr = new StringBuilder();
        for (int i = 0; i < goodsTagList.size(); i++) {
            idsStr.append(goodsTagList.get(i).getTagId().toString());  // 拼接标签ID
            if (i != goodsTagList.size() - 1) {
                idsStr.append(",");
            }
        }
        // 获取所有标签（批量查询标签）
        List<Tag> tags = mapper.getTagByIds(idsStr.toString());

        // 创建标签名称列表并直接从 tags 中获取名称
        List<String> tagNames = new ArrayList<>();
        for (GoodsTagRelation relation : goodsTagList) {
            for (Tag tag : tags) {
                if (tag.getId().equals(relation.getTagId())) {
                    tagNames.add(tag.getName());  // 根据关联的 tagId 获取 tag 名称
                    break;  // 找到对应的标签后退出内层循环
                }
            }
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


