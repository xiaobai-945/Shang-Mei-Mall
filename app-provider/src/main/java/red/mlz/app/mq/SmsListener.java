package red.mlz.app.mq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import red.mlz.app.module.sms_crond.service.SmsCrondService;
import red.mlz.common.module.sms_crond.entity.SmsCrond;

@Component
public class SmsListener {

    @Autowired
    private SmsCrondService smsCrondService;

    @RabbitListener(queues = RabbitMQConfig.SMS_QUEUE)
    public void receiveSmsMessage(SmsCrond smsTask) {
        // 调用服务发送短信
        smsCrondService.sendSmsSync(smsTask.getPhone());
    }
}