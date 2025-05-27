package red.mlz.app.controller.goods;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import red.mlz.app.module.goods.service.CategoryService;
import red.mlz.app.module.goods.service.GoodsService;
import red.mlz.app.module.tag.service.TagService;
import red.mlz.common.module.goods.dto.GoodsDTO;
import red.mlz.common.module.goods.entity.Category;
import red.mlz.common.module.goods.entity.Goods;

import java.math.BigInteger;
import java.util.*;


@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TagService tagService;


    /**
     * 类目列表
     */

    @RequestMapping("/goods/category_list/parentAll")
    public List<Category> getCategoryParentAll() {

        // 获取父类目数据
        List<Category> parentCategory = categoryService.getByParentAll();

        return parentCategory;

    }

    @RequestMapping("goods/category_list/parentId")
    public List<Category> getCategoryAll(@RequestParam(name = "parentId") BigInteger parentId) {
        // 获取父类目下子类目数据
        List<Category> categoryList = categoryService.getCategoryAll(parentId);
        return categoryList;
    }

    /**
     * 商品多级类目
     */

    @RequestMapping("/category/data")
    public List<Category> getCategoryData() {

        // 获取类目数据
        List<Category> parentCategory = categoryService.getCategories();
        return parentCategory;
    }

    @RequestMapping("/goods/data")
    public List<Goods> getGoodsData(@RequestParam(name = "categoryId") BigInteger categoryId,
                                    @RequestParam(name = "page") int page,
                                    @RequestParam(name = "pageSize") int pageSize) {
        // 获取商品数据

        List<Goods> goodsList = categoryService.getGoodsByCategoryId(categoryId, page, pageSize);
        return goodsList;
    }

    @RequestMapping("category/list")
    public List<Category> getByIds(@RequestParam(name = "ids") List<BigInteger> ids) {
        List<Category> categoryList = categoryService.getByIds(ids);
        return categoryList;
    }


    // 商品列表分页
    @RequestMapping("/goods/list")
    public List<Goods> getGoodsAll(@RequestParam(name = "keyword", required = false) String keyword,
                                   @RequestParam(name = "page") int page,
                                   @RequestParam(name = "pageSize") int pageSize) {

        // 获取商品数据
        List<Goods> goodsList = goodsService.getAllGoodsInfo(keyword, page, pageSize);
        return goodsList;
    }

    // 商品分页列表-联表查询
    @RequestMapping("/goods/new_list")
    public List<GoodsDTO> getGoodsList(@RequestParam(name = "keyword", required = false) String keyword,
                                       @RequestParam(name = "page") int page,
                                       @RequestParam(name = "pageSize") int pageSize) {


        // 获取商品数据
        List<GoodsDTO> goodsList = goodsService.getAllGoods(keyword, page, pageSize);

        return goodsList;

    }


    @RequestMapping("/goods/info")
    public Goods goodsInfo(@RequestParam(name = "goodsId") BigInteger goodsId) {
        // 获取商品信息
        Goods goods = goodsService.getById(goodsId);
        return goods;
    }

    @RequestMapping("/category/info")
    public Category categoryInfo(@RequestParam(name = "categoryId") BigInteger categoryId) {
        return categoryService.getById(categoryId);
    }

    @RequestMapping("goods/tags")
    public List<String> getTags(@RequestParam(name = "goodsId") BigInteger goodsId) {
        // 获取商品标签列表

        List<String> tags = tagService.getGoodsTags(goodsId);
        return tags;

    }

}
