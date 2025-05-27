package red.mlz.console.controller.goods;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import red.mlz.common.module.goods.entity.Goods;

import red.mlz.console.module.goods.service.CategoryService;
import red.mlz.console.module.goods.service.GoodsService;

import java.math.BigInteger;

import java.util.List;


@RestController
public class ConsoleGoodsController {
    @Autowired
    private GoodsService goodsService;


    @RequestMapping("goods/console_list")
    public List<Goods> getConsoleAll(@RequestParam(name = "keyword", required = false) String keyword,
                                     @RequestParam(name = "page", defaultValue = "1") int page,
                                     @RequestParam(name = "pageSize") int pageSize) {

        // 获取商品数据
        List<Goods> consoleList = goodsService.getAllGoodsInfo(keyword, page, pageSize);
        return consoleList;
    }

    @RequestMapping("goods/console_list/total")
    public int getConsoleTotal(@RequestParam(name = "keyword", required = false) String keyword) {
        int total = goodsService.getGoodsTotalForConsole(keyword);
        return total;
    }

    @RequestMapping("/goods/console_info")
    public Goods consoleInfoVo(@RequestParam(name = "goodsId") BigInteger goodsId) {

        return goodsService.getById(goodsId);

    }

    @RequestMapping("/goods/add")
    public BigInteger addGoods(@RequestParam(name = "goodsId", required = false) BigInteger goodsId,
                               @RequestParam(name = "categoryId") BigInteger categoryId,
                               @RequestParam(name = "title") String title,
                               @RequestParam(name = "goodsImages") String goodsImages,
                               @RequestParam(name = "sales") Integer sales,
                               @RequestParam(name = "goodsName") String goodsName,
                               @RequestParam(name = "price") Integer price,
                               @RequestParam(name = "source") String source,
                               @RequestParam(name = "sevenDayReturn") Integer sevenDayReturn,
                               @RequestParam(name = "goodsDetails", required = false) String goodsDetails,
                               @RequestParam(name = "tags") String tagNames) {


        BigInteger result = goodsService.edit(goodsId, categoryId, title, goodsImages, sales, goodsName, price, source, sevenDayReturn, goodsDetails, tagNames);
        return result;

    }

    @RequestMapping("/goods/update")
    public BigInteger updateGoods(@RequestParam(name = "goodsId") BigInteger goodsId,
                                  @RequestParam(name = "categoryId") BigInteger categoryId,
                                  @RequestParam(name = "title") String title,
                                  @RequestParam(name = "goodsImages") String goodsImages,
                                  @RequestParam(name = "sales") Integer sales,
                                  @RequestParam(name = "goodsName") String goodsName,
                                  @RequestParam(name = "price") Integer price,
                                  @RequestParam(name = "source") String source,
                                  @RequestParam(name = "sevenDayReturn") Integer sevenDayReturn,
                                  @RequestParam(name = "goodsDetails", required = false) String goodsDetails,
                                  @RequestParam(name = "tags", required = false) String tagNames) {


        BigInteger result = goodsService.edit(goodsId, categoryId, title.trim(), goodsImages, sales, goodsName.trim(), price, source.trim(), sevenDayReturn, goodsDetails, tagNames);
        return result;

    }

    @RequestMapping("/goods/delete")
    public BigInteger goodsDelete(@RequestParam(name = "goodsId") BigInteger goodsId) {

        BigInteger result = BigInteger.valueOf(goodsService.deleteGoods(goodsId));
        return result;

    }
}


