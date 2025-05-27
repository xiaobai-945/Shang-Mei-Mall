package red.mlz.app.api.goods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import red.mlz.common.module.goods.dto.GoodsDTO;
import red.mlz.common.module.goods.entity.Category;
import red.mlz.common.module.goods.entity.Goods;

import java.math.BigInteger;
import java.util.List;


@FeignClient(name="app-provider")
public interface GoodsFeignClient {


    /**
     * 类目列表
     */

    @RequestMapping("/goods/category_list/parentAll")
    List<Category> getCategoryParentAll();


    @RequestMapping("goods/category_list/parentAll")
    List<Category> getCategoryAll(@RequestParam(name = "parentId") BigInteger parentId);

    /**
     * 商品多级类目
     */

    @RequestMapping("/category/data")
    List<Category> getCategoryData();

    @RequestMapping("/goods/data")
    List<Goods> getGoodsData(@RequestParam(name = "categoryId") BigInteger categoryId,
                                    @RequestParam(name = "page") int page,
                                    @RequestParam(name = "pageSize") int pageSize);

    @RequestMapping("category/list")
    List<Category> getByIds(@RequestParam(name = "ids") List<BigInteger> ids);

    // 商品列表分页
    @RequestMapping("/goods/list")
    List<Goods> getGoodsAll(@RequestParam(name = "keyword",required = false) String keyword,
                                   @RequestParam(name = "page") int page,
                                   @RequestParam(name = "pageSize") int pageSize);

    // 商品分页列表-联表查询
    @RequestMapping("/goods/new_list")
    List<GoodsDTO> getGoodsList(@RequestParam(name = "keyword",required = false) String keyword,
                                       @RequestParam(name = "page") int page,
                                       @RequestParam(name = "pageSize") int pageSize);

    @RequestMapping("/goods/info")
    Goods goodsInfo(@RequestParam(name = "goodsId") BigInteger goodsId);

    @RequestMapping("/category/info")
    Category categoryInfo (@RequestParam(name = "categoryId") BigInteger categoryId);

    @RequestMapping("goods/tags")
    List<String> getTags(@RequestParam(name = "goodsId") BigInteger goodsId);

}


