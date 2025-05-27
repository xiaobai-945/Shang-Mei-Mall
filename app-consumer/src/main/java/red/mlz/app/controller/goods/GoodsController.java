package red.mlz.app.controller.goods;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import red.mlz.app.api.goods.GoodsFeignClient;
import red.mlz.app.domain.BaseContentValueVo;
import red.mlz.app.domain.BaseListVo;
import red.mlz.app.domain.goods.*;
import red.mlz.common.module.goods.dto.GoodsDTO;
import red.mlz.common.module.goods.entity.Category;
import red.mlz.common.module.goods.entity.Goods;

import red.mlz.common.utils.*;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;


@RestController
public class GoodsController {

    @Resource
    private GoodsFeignClient goodsFeignClient;

    @Autowired
    private RedisTemplate<String,Object>  redisTemplate;
    /**
     * 类目列表
     */

    @RequestMapping("/goods/category_list")
    public Response getCategoryAll() {

        // 获取父类目数据
        List<Category> parentCategory = goodsFeignClient.getCategoryParentAll();

        // 创建父类目展示对象
        List<ParentCategoryVo> parentCategoryV0List = new ArrayList<>();
        for (Category parentCategories : parentCategory) {
            ParentCategoryVo parentCategoryVo = new ParentCategoryVo();
            parentCategoryVo.setId(parentCategories.getId());
            parentCategoryVo.setName(parentCategories.getName());
            parentCategoryVo.setImage(parentCategories.getImage());

            // 获取父类目下子类目数据
            List<Category> categoryList = goodsFeignClient.getCategoryAll(parentCategories.getId());

            // 创建子类目展示对象
            List<CategoryItemVo> categoryItemVoList = new ArrayList<>();
            for (Category category : categoryList) {
                CategoryItemVo categoryItemVo = new CategoryItemVo();
                categoryItemVo.setId(category.getId());
                categoryItemVo.setParentId(category.getParentId());
                categoryItemVo.setName(category.getName());
                categoryItemVo.setImage(category.getImage());
                categoryItemVoList.add(categoryItemVo);
            }
            // 子类目列表放在父类目中
            parentCategoryVo.setCategoryList(categoryItemVoList);

            // 将parentCategoryV0内容添加到parentCategoryV0List列表
            parentCategoryV0List.add(parentCategoryVo);

        }

        ParentVo parentVo = new ParentVo();
        parentVo.setParentList(parentCategoryV0List);
        return new Response<>(1001, parentVo);

    }

    @RequestMapping("/goods/category_goods")
    public Response getCategoryGoodsItem(@RequestParam(name = "categoryId", required = false) BigInteger categoryId,
                                         @RequestParam(name = "wp", required = false) String wp) {

        GoodsWpVo baseWp = new GoodsWpVo();

        String pageSize = SpringUtils.getProperty("application.pagesize");

        if(!BaseUtils.isEmpty(wp)){
            byte[] bytes = Base64.getUrlDecoder().decode(wp.getBytes(StandardCharsets.UTF_8));
            String realWp = new String(bytes, StandardCharsets.UTF_8);
            try{
                baseWp = JSON.parseObject(realWp, GoodsWpVo.class);
            }catch (Exception e){
                return new Response(4004);
            }
        }else{

            baseWp.setPage(1);
            baseWp.setPageSize(Integer.valueOf(pageSize));
        }


        // 获取类目数据
        List<Category> parentCategory = goodsFeignClient.getCategoryParentAll();

        List<CategoryVo> categories = parentCategory.stream()
                .map(parentCategories -> {
                    CategoryVo categoryVO = new CategoryVo();
                    categoryVO.setId(parentCategories.getId());
                    categoryVO.setName(parentCategories.getName());
                    categoryVO.setImage(parentCategories.getImage());
                    return categoryVO;
                }).collect(Collectors.toList());


        // 获取商品数据
        List<Goods> goodsList = goodsFeignClient.getGoodsData(categoryId, baseWp.getPage(), baseWp.getPageSize());

        // 判断是否是最后一页（分页结束），如果当前页获取到的商品数量小于每页数量说明分页结束
        BaseListVo result = new BaseListVo();
        Boolean isEnd = Integer.parseInt(pageSize) > goodsList.size();

        result.setIsEnd(isEnd);
        baseWp.setPage(baseWp.getPage()+1);
        String jsonWp = JSONObject.toJSONString(baseWp);
        byte[] encodeWp = Base64.getUrlEncoder().encode(jsonWp.getBytes(StandardCharsets.UTF_8));
        result.setWp(new String(encodeWp, StandardCharsets.UTF_8).trim());

        List<GoodsListVo> list = new ArrayList<>();

        // 获取商品分类id
        List<BigInteger> ids = goodsList.stream()
                .map(Goods::getCategoryId)  // 提取每个商品的分类ID
                .collect(Collectors.toList());  // 将结果收集到一个List中

        List<Category> categoryList = goodsFeignClient.getByIds(ids);

        // 创建 HashMap
        Map<BigInteger, String> categoryMap = new HashMap<>();
        // 循环分类列表
        for (Category category : categoryList) {
            // 上传HashMap的键值对
            categoryMap.put(category.getId(), category.getName());
        }


        // 遍历商品列表，将每个商品转换为 goodsItemVO
        for (Goods goods : goodsList) {

            GoodsListVo goodsItemVo = new GoodsListVo();

            // 判断类目id是否为空，若为空跳过商品，若不为空则在map里获取类目信息
            String categoryName = categoryMap.get(goods.getCategoryId());

            if (categoryName == null) {
                continue;
            }

            // 将轮播图图片用 “ $ ” 连接
            String[] images = goods.getGoodsImages().split("\\$");


            // 获取图片信息，包含 AR 和 URL
            ImageInfo imageInfo = ImageUtils.getImageInfo(images[0]);

            goodsItemVo.setId(goods.getId())
                    .setCategoryName(categoryName)
                    .setGoodsImage(imageInfo)
                    .setTitle(goods.getTitle())
                    .setPrice(goods.getPrice())
                    .setSales(goods.getSales());

            list.add(goodsItemVo);

        }
        result.setList(list);



        // 5. 返回最终数据
        CategoryGoodsVo categoryGoodsVO = new CategoryGoodsVo();
        categoryGoodsVO.setCategories(categories);  // 类目列表
        categoryGoodsVO.setGoodsItem(result);  // 商品分页列表

        return new Response(1001, categoryGoodsVO);

    }


    @RequestMapping("/goods/list")
    public Response getGoodsAll(@RequestParam(name = "keyword", required = false) String keyword,
                                @RequestParam(name = "wp", required = false) String wp) {

        GoodsWpVo baseWp = new GoodsWpVo();
//        String pageSize = SpringUtils.getProperty("application.pagesize");

        if(!BaseUtils.isEmpty(wp)){
            byte[] bytes = Base64.getUrlDecoder().decode(wp.getBytes(StandardCharsets.UTF_8));
            String realWp = new String(bytes, StandardCharsets.UTF_8);
            try{
                baseWp = JSON.parseObject(realWp, GoodsWpVo.class);
            }catch (Exception e){
                return new Response(4004);
            }
        }else{
            baseWp.setPage(1);
            baseWp.setPageSize(Integer.valueOf("10"));
            baseWp.setName(keyword);
        }


        BaseListVo result = new BaseListVo();


        // 创建缓存的 key
        String cacheKey = "goodsList:" + baseWp.getName() + ":" + baseWp.getPage() + ":" + baseWp.getPageSize();
        // 检查缓存中是否有数据

        String resultCache = (String) redisTemplate.opsForValue().get(cacheKey);

        if (resultCache != null) {
            // 如果缓存中有数据，使用 JSON 反序列化为 List<GoodsListVo>
            BaseListVo cachedBaseListVo = JSON.parseObject(resultCache, BaseListVo.class);

            return new Response<>(1001, cachedBaseListVo);
        } else {
            // 获取商品数据
            List<Goods> goodsList = goodsFeignClient.getGoodsAll(baseWp.name, baseWp.getPage(), baseWp.getPageSize());

            // 判断是否是最后一页（分页结束），如果当前页获取到的商品数量小于每页数量说明分页结束
            result.setIsEnd(baseWp.getPageSize() > goodsList.size());

            baseWp.setPage(baseWp.getPage() + 1);
            String jsonWp = JSONObject.toJSONString(baseWp);
            byte[] encodeWp = Base64.getUrlEncoder().encode(jsonWp.getBytes(StandardCharsets.UTF_8));
            result.setWp(new String(encodeWp, StandardCharsets.UTF_8).trim());

            // 创建商品展示对象列表
            List<GoodsListVo> goodsVoList = new ArrayList<>();

            // 获取商品分类id
            List<BigInteger> ids = goodsList.stream()
                    .map(Goods::getCategoryId)  // 提取每个商品的分类ID
                    .collect(Collectors.toList());  // 将结果收集到一个List中

            List<Category> categories = goodsFeignClient.getByIds(ids);

            // 创建 HashMap
            Map<BigInteger, String> categoryMap = new HashMap<>();
            // 循环分类列表
            for (Category category : categories) {

                // 上传HashMap的键值对
                categoryMap.put(category.getId(), category.getName());
            }

            // 遍历商品列表，将每个商品转换为 goodsItemVO
            for (Goods goods : goodsList) {

                GoodsListVo goodsItemVo = new GoodsListVo();

                // 判断类目id是否为空，若为空跳过商品，若不为空则在map里获取类目信息
                String categoryName = categoryMap.get(goods.getCategoryId());

                if (categoryName == null) {
                    continue;
                }

                // 将轮播图图片用 “ $ ” 连接
                String[] images = goods.getGoodsImages().split("\\$");

                // 获取图片信息，包含 AR 和 URL
//            ImageInfo imageInfo = Utility.getImageInfo(images[0]);
                ImageInfo imageInfo = ImageUtils.getImageInfo(images[0]);

                goodsItemVo.setId(goods.getId())
                        .setCategoryName(categoryName)
                        .setGoodsImage(imageInfo)
                        .setTitle(goods.getTitle())
                        .setPrice(goods.getPrice())
                        .setSales(goods.getSales());
                goodsVoList.add(goodsItemVo);

            }
            result.setList(goodsVoList);
            // 将查询结果存入 Redis，设置过期时间为 5 分钟
            redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(result), Duration.ofMinutes(5));
        }
        return new Response<> (1001, result);
    }

    @RequestMapping("/goods/new_list")
    public Response getGoodsList(@RequestParam(name = "keyword", required = false) String keyword,
                                 @RequestParam(name = "wp", required = false) String wp) {


        GoodsWpVo baseWp = new GoodsWpVo();
        String pageSize = SpringUtils.getProperty("application.pagesize");

        if(!BaseUtils.isEmpty(wp)){
            byte[] bytes = Base64.getUrlDecoder().decode(wp.getBytes(StandardCharsets.UTF_8));
            String realWp = new String(bytes, StandardCharsets.UTF_8);
            try{
                baseWp = JSON.parseObject(realWp, GoodsWpVo.class);
            }catch (Exception e){
                return new Response(4004);
            }
        }else{
            baseWp.setPage(1);
            baseWp.setPageSize(Integer.valueOf(pageSize));
            baseWp.setName(keyword);
        }

        // 获取商品数据
        List<GoodsDTO> goodsList = goodsFeignClient.getGoodsList(baseWp.getName(), baseWp.getPage(), baseWp.getPageSize());

        // 判断是否是最后一页（分页结束），如果当前页获取到的商品数量小于每页数量说明分页结束
        BaseListVo result = new BaseListVo();

        result.setIsEnd(Integer.parseInt(pageSize) > goodsList.size());
        baseWp.setPage(baseWp.getPage()+1);
        String jsonWp = JSONObject.toJSONString(baseWp);
        byte[] encodeWp = Base64.getUrlEncoder().encode(jsonWp.getBytes(StandardCharsets.UTF_8));
        result.setWp(new String(encodeWp, StandardCharsets.UTF_8).trim());

        // 创建商品展示对象列表
        List<GoodsListVo> goodsVoList = new ArrayList<>();

        // 遍历商品列表，将每个商品转换为 goodsItemVO
        for (GoodsDTO goodsDTO : goodsList) {

            GoodsListVo goodsItemVo = new GoodsListVo();

            // 将轮播图图片用 “ $ ” 连接
            String[] images = goodsDTO.getGoodsImages().split("\\$");



            // 获取图片信息，包含 AR 和 URL
            ImageInfo imageInfo = ImageUtils.getImageInfo(images[0]);


            goodsItemVo.setId(goodsDTO.getId())
                    .setCategoryName(goodsDTO.getCategoryName())
                    .setGoodsImage(imageInfo)
                    .setTitle(goodsDTO.getTitle())
                    .setPrice(goodsDTO.getPrice())
                    .setSales(goodsDTO.getSales());

            goodsVoList.add(goodsItemVo);

        }
        result.setList(goodsVoList);


        return new Response<> (1001, result);

    }

    @RequestMapping("/goods/info")
    public Response goodsInfo(@RequestParam(name = "goodsId") BigInteger goodsId) {
        Goods goods = goodsFeignClient.goodsInfo(goodsId);

        if (BaseUtils.isEmpty(goods)) {
            return new Response(4004);
        }

        // 类目
        Category category = goodsFeignClient.categoryInfo(goods.getCategoryId());
        String categoryName = category != null ? category.getName() : "未分类";
        String categoryImages = category != null ? category.getImage() : "未上传类目图";

        // 获取商品标签列表

        List<String> tags;
        try {
            tags = goodsFeignClient.getTags(goodsId);
        } catch (Exception e) {
            return new Response(4004);
        }


        GoodsInfoVo goodsInfoVo = new GoodsInfoVo();
        // 创建 GoodsInfoVo 并设置相应的字段
        //返回类目名和类目图
        goodsInfoVo.setCategoryName(categoryName);
        goodsInfoVo.setCategoryImage(categoryImages);

        // 设置商品图片轮播图，将商品图片字符串转换为 List
        String[] imagesArray = goods.getGoodsImages().split("\\$");
        goodsInfoVo.setGoodsImages(Arrays.asList(imagesArray));
        // 返回商品信息
        goodsInfoVo.setSource(goods.getSource());
        goodsInfoVo.setPrice(goods.getPrice());
        goodsInfoVo.setSales(goods.getSales());
        goodsInfoVo.setGoodsName(goods.getGoodsName());
        goodsInfoVo.setSevenDayReturn(goods.getSevenDayReturn());
        goodsInfoVo.setTags(tags);

        try {
            List<BaseContentValueVo> contents = JSON.parseArray(goods.getGoodsDetails(), BaseContentValueVo.class);
            goodsInfoVo.setContent(contents);
        } catch (Exception cause) {
            return new Response(4004);
        }
        return new Response<>(1001,goodsInfoVo);
    }

}
