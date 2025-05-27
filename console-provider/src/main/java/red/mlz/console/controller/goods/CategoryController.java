package red.mlz.console.controller.goods;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import red.mlz.common.config.ReadOnly;
import red.mlz.common.module.goods.entity.Category;


import red.mlz.common.utils.Response;
import red.mlz.console.module.goods.service.CategoryService;
import red.mlz.console.module.goods.service.GoodsService;
import red.mlz.console.mq.RabbitMQConfig;

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
     @Autowired
     private CategoryService service;

     @Autowired
     private GoodsService goodsService;

     @Autowired
     private RabbitTemplate rabbitTemplate;

     @RequestMapping("/category/info")
     @ReadOnly
     public Category categoryInfo(@RequestParam(name = "categoryId") BigInteger categoryId) {
          return service.getById(categoryId);
     }

     @RequestMapping("/category/all")
     @ReadOnly
     public List<Category> categoryAll() {
          return service.getAll();
     }

     @RequestMapping("/category/add")
     public Response insertCategory(@RequestParam(name = "parentId") BigInteger parentId, @RequestParam(name = "name") String name, @RequestParam(name = "image") String image) {

          try {
               service.insert(parentId, name, image);
               return new Response<>(1001);

          } catch (Exception e) {
               return new Response<>(1002);
          }


     }

     @RequestMapping("/category/update")
     public Response updateCategory(@RequestParam(name = "categoryId") BigInteger categoryId,
                                    @RequestParam(name = "name") String name,
                                    @RequestParam(name = "image") String image) {

          try {

               service.update(categoryId, name, image);
               return new Response<>(1001);
          } catch (Exception e) {
               return new Response<>(4005);
          }

     }

     @RequestMapping("/category/delete")
     public Response delete(@RequestParam(name = "categoryId") BigInteger categoryId) {


          // 删除分类
          int category = service.delete(categoryId);

          // 发送消息到消息队列，异步删除内容
          rabbitTemplate.convertAndSend(RabbitMQConfig.GOODS_DELETE_QUEUE, categoryId.toString());

          // 返回
          if (category == 1) {
               return new Response(1001); // 分类删除成功
          } else {
               return new Response(3002); // 分类删除失败
          }
     }
}
