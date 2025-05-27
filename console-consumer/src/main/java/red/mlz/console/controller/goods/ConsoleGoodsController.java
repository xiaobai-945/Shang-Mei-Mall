package red.mlz.console.controller.goods;


import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import red.mlz.common.module.goods.entity.Category;
import red.mlz.common.module.goods.entity.Goods;
import red.mlz.common.utils.BaseUtils;
import red.mlz.common.utils.Response;
import red.mlz.common.utils.SpringUtils;
import red.mlz.console.api.goods.CategoryFeignClient;
import red.mlz.console.api.goods.ConsoleGoodsFeignClient;
import red.mlz.console.domain.BaseContentValueVo;
import red.mlz.console.domain.BaseListVo;
import red.mlz.console.domain.goods.*;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@RestController
public class ConsoleGoodsController {
    @Resource
    private ConsoleGoodsFeignClient consoleGoodsFeignClient;

    @Resource
    private CategoryFeignClient categoryFeignClient;

    @RequestMapping("goods/console_list")
    public Response getConsoleAll(@RequestParam(name = "keyword", required = false) String keyword,
                                  @RequestParam(name = "page", defaultValue = "1") int page) {


        // 获取商品数据

        String pageSize = SpringUtils.getProperty("application.pagesize");
        List<Goods> consoleList = consoleGoodsFeignClient.getConsoleAll(keyword,page, Integer.parseInt(pageSize));
        int total = consoleGoodsFeignClient.getConsoleTotal(keyword);

        BaseListVo result = new BaseListVo();
        result.setTotal(total);
        result.setPageSize(Integer.valueOf(pageSize));

        // 创建商品展示对象列表

        List<GoodsListVo> consoleItemVoList = new ArrayList<>();

        for (Goods goods : consoleList) {
            GoodsListVo consoleItemVo = new GoodsListVo();

            if (!BaseUtils.isEmpty(goods.getGoodsImages())) {
                String[] images = goods.getGoodsImages().split("\\$");
                consoleItemVo.setGoodImage(images[0]);
            }

            consoleItemVo.setId(goods.getId())
                    .setTitle(goods.getTitle())
                    .setSales(goods.getSales())
                    .setPrice(goods.getPrice());
            consoleItemVoList.add(consoleItemVo);
        }
        result.setList(consoleItemVoList);


        return new Response<>(1001, result);
    }

    @RequestMapping("/goods/console_info")
    public Response consoleInfoVo(@RequestParam(name = "goodsId") BigInteger goodsId) {

        Goods goods = consoleGoodsFeignClient.consoleInfoVo(goodsId);
//        if (BaseUtils.isEmpty(goods)) {
//            return new Response(4004);
//        }

        GoodsInfoVo consoleInfo = new GoodsInfoVo();

        // 设置商品图片轮播图，将商品图片字符串转换为 List
        String[] imagesArray = goods.getGoodsImages().split("\\$");
        consoleInfo.setGoodsImages(Arrays.asList(imagesArray));

        consoleInfo.setPrice(goods.getPrice());
        consoleInfo.setSales(goods.getSales());
        consoleInfo.setGoodsName(goods.getGoodsName());
        consoleInfo.setSevenDayReturn(goods.getSevenDayReturn());
        try {
            List<BaseContentValueVo> contents = JSON.parseArray(goods.getGoodsDetails(), BaseContentValueVo.class);
            consoleInfo.setContent(contents);
        } catch (Exception cause) {
            // ignores
            return new Response(4004);
        }
        consoleInfo.setCreatedTime(BaseUtils.timeStamp2Date(goods.getCreatedTime()));
        consoleInfo.setUpdatedTime(BaseUtils.timeStamp2Date(goods.getUpdatedTime()));

        return new Response<>(1001, consoleInfo);

    }

    @RequestMapping("/goods/console/categories/tree")
    public Response categoryTree() {

        // 获取所有类目
        List<Category> categories = categoryFeignClient.categoryAll();

        List<CategoryVo> categoryList = buildCategoryTree(categories, null);

        // 封装返回结果
        CategoryTree categoryTree = new CategoryTree();
        categoryTree.setData(categoryList);

        return new Response<>(1001, categoryTree);
    }

    // 递归构建类目树
    private List<CategoryVo> buildCategoryTree(List<Category> categories, BigInteger parentId) {
        List<CategoryVo> result = new ArrayList<>();
        for (Category category : categories) {
            if (category.getParentId() == null && parentId == null || category.getParentId() != null && category.getParentId().equals(parentId)) {
                // 构建子类目树
                CategoryVo categoryVO = new CategoryVo();
                categoryVO.setId(category.getId());
                categoryVO.setName(category.getName());
                categoryVO.setChildren(buildCategoryTree(categories, category.getId()));  // 递归获取子类目
                result.add(categoryVO);
            }
        }
        return result;

    }

    @RequestMapping("/goods/add")
    public Response addGoods(@RequestParam(name = "goodsId", required = false) BigInteger goodsId,
                             @RequestParam(name = "categoryId") BigInteger categoryId,
                             @RequestParam(name = "title") String title,
                             @RequestParam(name = "goodsImages") String goodsImages,
                             @RequestParam(name = "sales") Integer sales,
                             @RequestParam(name = "goodsName") String goodsName,
                             @RequestParam(name = "price") Integer price,
                             @RequestParam(name = "source") String source,
                             @RequestParam(name = "sevenDayReturn") Integer sevenDayReturn,
                             @RequestParam(name = "goodsDetails",required = false) String goodsDetails,
                             @RequestParam(name = "tags") String tagNames) {


        if (BaseUtils.isEmpty(categoryId)) {
            return new Response(3051);
        }
        //parameters check
        title = title.trim();
        if (BaseUtils.isEmpty(title)) {
            return new Response(3051);
        }

        if (!BaseUtils.isEmpty(categoryId)) {
            Category category = categoryFeignClient.categoryInfo(categoryId);
            if (BaseUtils.isEmpty(category)) {
                return new Response(3052);
            }
        }

        ConsoleVo consoleVo = new ConsoleVo();
        try {
            BigInteger result = consoleGoodsFeignClient.addGoods(goodsId, categoryId, title, goodsImages, sales, goodsName, price, source, sevenDayReturn,goodsDetails,tagNames);
            consoleVo.setId(result.toString());
            return new Response(1001, consoleVo);
        } catch (Exception exception) {
            System.out.println(exception);
            return new Response(4004);

        }

    }

    @RequestMapping("/goods/update")
    public Response updateGoods(@RequestParam(name = "goodsId") BigInteger goodsId,
                                @RequestParam(name = "categoryId") BigInteger categoryId,
                                @RequestParam(name = "title") String title,
                                @RequestParam(name = "goodsImages") String goodsImages,
                                @RequestParam(name = "sales") Integer sales,
                                @RequestParam(name = "goodsName") String goodsName,
                                @RequestParam(name = "price") Integer price,
                                @RequestParam(name = "source") String source,
                                @RequestParam(name = "sevenDayReturn") Integer sevenDayReturn,
                                @RequestParam(name = "goodsDetails",required = false) String goodsDetails,
                                @RequestParam(name = "tags", required = false) String tagNames) {


        if (!BaseUtils.isEmpty(categoryId)) {
            Category category = categoryFeignClient.categoryInfo(categoryId);
            if (BaseUtils.isEmpty(category)) {
                return new Response(3052);
            }
        }
        if (BaseUtils.isEmpty(goodsId)) {
            return new Response(3051);
        }
        //parameters check
        title = title.trim();
        if (BaseUtils.isEmpty(title)) {
            return new Response(3051);
        }

        // 调用 service 层修改商品
        ConsoleVo consoleVo = new ConsoleVo();
        try {

            BigInteger result = consoleGoodsFeignClient.updateGoods(goodsId, categoryId, title.trim(), goodsImages, sales, goodsName.trim(), price, source.trim(), sevenDayReturn,goodsDetails,tagNames);

            consoleVo.setId(result.toString());
            return new Response(1001, consoleVo);

        } catch (Exception exception) {
            return new Response(4004);
        }

    }

    @RequestMapping("/goods/delete")
    public Response goodsDelete(@RequestParam(name = "goodsId") BigInteger goodsId) {

        if (BaseUtils.isEmpty(goodsId)) {
            return new Response(4004);
        }
        try {
            consoleGoodsFeignClient.goodsDelete(goodsId);
            return new Response(1001);
        } catch (Exception exception) {
            return new Response(4004);
        }

    }
}


