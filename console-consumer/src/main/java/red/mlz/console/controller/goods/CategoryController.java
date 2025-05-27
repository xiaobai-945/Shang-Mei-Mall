package red.mlz.console.controller.goods;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import red.mlz.common.config.ReadOnly;
import red.mlz.common.module.goods.entity.Category;
import red.mlz.common.utils.Response;
import red.mlz.console.api.goods.CategoryFeignClient;

import javax.annotation.Resource;
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
@RestController
public class CategoryController {
     @Resource
     private CategoryFeignClient categoryFeignClient;

     @RequestMapping("/category/info")
     @ReadOnly
     public Category categoryInfo (@RequestParam(name = "categoryId") BigInteger categoryId) {
          return categoryFeignClient.categoryInfo(categoryId);
     }
     @RequestMapping("/category/all")
     @ReadOnly
     public List<Category> categoryAll(){
          return categoryFeignClient.categoryAll();
     }

     @RequestMapping("/category/add")
     public Response insertCategory(@RequestParam(name = "parentId")BigInteger parentId,@RequestParam(name = "name") String name, @RequestParam(name = "image") String image) {

         return categoryFeignClient.insertCategory(parentId,name,image);


     }
     @RequestMapping("/category/update")
     public Response updateCategory(@RequestParam(name = "categoryId") BigInteger categoryId,
                                  @RequestParam(name = "name") String name,
                                  @RequestParam(name = "image") String image) {

          return categoryFeignClient.updateCategory(categoryId,name,image);

     }

     @RequestMapping("/category/delete")
     public Response delete (@RequestParam(name = "categoryId") BigInteger categoryId) {


          return categoryFeignClient.delete(categoryId);
     }

}
