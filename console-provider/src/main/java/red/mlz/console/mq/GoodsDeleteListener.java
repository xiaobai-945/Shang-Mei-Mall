package red.mlz.console.mq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import red.mlz.console.module.goods.service.GoodsService;

import java.math.BigInteger;


@Component
public class GoodsDeleteListener {

    @Autowired
    private GoodsService goodsService;

    @RabbitListener(queues = RabbitMQConfig.GOODS_DELETE_QUEUE)
    public void receiveGoodsDeleteMessage(String categoryId) {
        try {
            BigInteger categoryIdBigInt = new BigInteger(categoryId);
            // 调用服务删除内容
            goodsService.deleteGoods(categoryIdBigInt);
        } catch (Exception e) {
            // 日志记录异常
            e.printStackTrace();
        }
    }
}