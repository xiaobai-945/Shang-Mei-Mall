package red.mlz.app.module.goods_tag_relation.service;

import org.springframework.stereotype.Service;
import red.mlz.app.module.goods_tag_relation.mapper.GoodsTagRelationMapper;
import red.mlz.common.config.ReadOnly;
import red.mlz.common.module.goods_tag_relation.entity.GoodsTagRelation;

import red.mlz.common.utils.BaseUtils;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.List;

@Service
public class GoodsTagRelationService {
    @Resource
    private GoodsTagRelationMapper relationMapper;


    // 获取所有标签列表
    @ReadOnly
    public List<GoodsTagRelation> getAll() {
        return relationMapper.getAll();
    }

    // 根据ID查询标签
    @ReadOnly
    public GoodsTagRelation getById(BigInteger id) {
        return relationMapper.getById(id);
    }

    // 根据商品ID获取所有关联的标签ID
    @ReadOnly
    public List<GoodsTagRelation> getTagIdByGoodsId(BigInteger goodsId) {
        return relationMapper.getTagIdByGoodsId(goodsId);
    }

    // 根据商品ID获取标签关联
    @ReadOnly
    public List<BigInteger> getByGoodsId(BigInteger goodsId) {
        return relationMapper.getTagIdGoodsId(goodsId);
    }

    // 根据ID提取标签
    @ReadOnly
    public GoodsTagRelation extractById(BigInteger id) {
        return relationMapper.extractById(id);
    }

    // 插入标签关联
    public int insert(BigInteger goodsId, BigInteger tagId) {

        GoodsTagRelation goodsTag = new GoodsTagRelation();

        goodsTag.setGoodsId(goodsId);
        goodsTag.setTagId(tagId);
        goodsTag.setCreatedTime(BaseUtils.currentSeconds());
        goodsTag.setUpdatedTime(BaseUtils.currentSeconds());
        goodsTag.setIsDeleted(0);
        return relationMapper.insert(goodsTag);

    }

    // 更新标签关联
    public int update(BigInteger id,BigInteger goodsId,BigInteger tagId) {
        GoodsTagRelation goodsTag = new GoodsTagRelation();
        goodsTag.setId(id);
        goodsTag.setGoodsId(goodsId);
        goodsTag.setGoodsId(goodsId);
        goodsTag.setTagId(tagId);
        goodsTag.setCreatedTime(BaseUtils.currentSeconds());
        goodsTag.setUpdatedTime(BaseUtils.currentSeconds());
        goodsTag.setIsDeleted(0);
        return relationMapper.update(goodsTag);
    }

    // 删除标签关联（按ID）
    public int deleteRelation(BigInteger goodsId, BigInteger tagId) {
        return relationMapper.deleteRelation(goodsId, tagId);
    }


}
