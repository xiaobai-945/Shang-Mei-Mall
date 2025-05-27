package red.mlz.app.controller.sms;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import red.mlz.app.module.sms_crond.service.SmsCrondService;
import red.mlz.common.utils.Response;

@RestController
public class SmsCrondController {

    @Autowired
    private SmsCrondService smsCrondService;


    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 多线程发送
     */
    @RequestMapping("send/thread")
    public Response sendThread(@RequestParam(name = "phoneNumbers") String phoneNumbers) {

        int result = smsCrondService.sendThread(phoneNumbers);

        return new Response(1001, result);

    }

    /**
     * 同步发送
     */
    @RequestMapping("send/sync")
    public Response sendSync(@RequestParam(name = "phone") String phone) {
        return new Response(1001, smsCrondService.sendSmsSync(phone));

    }

    /**
     * 异步发送
     */
    @RequestMapping("send/async")
    public Response sendAsync(@RequestParam(name = "phone") String phone) {

        return new Response(1001, smsCrondService.sendAsync(phone));

    }

}

