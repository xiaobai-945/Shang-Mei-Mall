package red.mlz.console.api.goods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import red.mlz.common.module.goods.entity.Category;
import red.mlz.common.module.goods.entity.Goods;
import red.mlz.common.utils.Response;
import java.math.BigInteger;
import java.util.List;


@FeignClient(name="console-provider")
public interface ConsoleGoodsFeignClient {

    @RequestMapping("goods/console_list")
    List<Goods> getConsoleAll(@RequestParam(name = "keyword", required = false) String keyword,
                              @RequestParam(name = "page", defaultValue = "1") int page,
                              @RequestParam(name = "pageSize",required = false) int pageSize);

    @RequestMapping("goods/console_list/total")
    int getConsoleTotal(@RequestParam(name = "keyword", required = false) String keyword);

    @RequestMapping("/goods/console_info")
    Goods consoleInfoVo(@RequestParam(name = "goodsId") BigInteger goodsId) ;

    @RequestMapping("/goods/add")
    BigInteger addGoods(@RequestParam(name = "goodsId", required = false) BigInteger goodsId,
                      @RequestParam(name = "categoryId") BigInteger categoryId,
                      @RequestParam(name = "title") String title,
                      @RequestParam(name = "goodsImages") String goodsImages,
                      @RequestParam(name = "sales") Integer sales,
                      @RequestParam(name = "goodsName") String goodsName,
                      @RequestParam(name = "price") Integer price,
                      @RequestParam(name = "source") String source,
                      @RequestParam(name = "sevenDayReturn") Integer sevenDayReturn,
                      @RequestParam(name = "goodsDetails", required = false) String goodsDetails,
                      @RequestParam(name = "tags") String tagNames) ;

    @RequestMapping("/goods/update")
    BigInteger updateGoods(@RequestParam(name = "goodsId") BigInteger goodsId,
                         @RequestParam(name = "categoryId") BigInteger categoryId,
                         @RequestParam(name = "title") String title,
                         @RequestParam(name = "goodsImages") String goodsImages,
                         @RequestParam(name = "sales") Integer sales,
                         @RequestParam(name = "goodsName") String goodsName,
                         @RequestParam(name = "price") Integer price,
                         @RequestParam(name = "source") String source,
                         @RequestParam(name = "sevenDayReturn") Integer sevenDayReturn,
                         @RequestParam(name = "goodsDetails", required = false) String goodsDetails,
                         @RequestParam(name = "tags", required = false) String tagNames) ;

    @RequestMapping("/goods/delete")
    BigInteger goodsDelete(@RequestParam(name = "goodsId") BigInteger goodsId) ;
}









