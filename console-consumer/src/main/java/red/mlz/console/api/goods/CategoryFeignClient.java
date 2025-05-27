package red.mlz.console.api.goods;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import red.mlz.common.module.goods.entity.Category;
import red.mlz.common.utils.Response;

import java.math.BigInteger;
import java.util.List;

/**
 * <p>
 * 类目表 前端控制器
 * </p>
 *
 * @author 小白-945
 * @since 2024-12-22
 */
@FeignClient(name="consoler-provider")
public interface CategoryFeignClient {


     @RequestMapping("/category/info")
     Category categoryInfo(@RequestParam(name = "categoryId") BigInteger categoryId);

     @RequestMapping("/category/all")
     List<Category> categoryAll();

     @RequestMapping("/category/add")
     Response insertCategory(@RequestParam(name = "parentId") BigInteger parentId, @RequestParam(name = "name") String name, @RequestParam(name = "image") String image) ;


     @RequestMapping("/category/update")
     Response updateCategory(@RequestParam(name = "categoryId") BigInteger categoryId,
                             @RequestParam(name = "name") String name,
                             @RequestParam(name = "image") String image);

     @RequestMapping("/category/delete")
     Response delete(@RequestParam(name = "categoryId") BigInteger categoryId);

}
