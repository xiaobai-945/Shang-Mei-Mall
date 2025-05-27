package red.mlz.app.controller.sms;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import red.mlz.app.api.sms.SmsCrondFeignClient;
import red.mlz.common.utils.Response;

import javax.annotation.Resource;

@RestController
public class SmsCrondController {

    @Resource
    private SmsCrondFeignClient smsCrondFeignClient;

    /**
     * 多线程发送
     */
    @RequestMapping("send/thread")
    public Response sendThread(@RequestParam(name = "phoneNumbers") String phoneNumbers){

        return smsCrondFeignClient.sendThread(phoneNumbers);

    }

    /**
     * 同步发送
     */
    @RequestMapping("send/sync")
    public Response sendSync(@RequestParam(name = "phone") String phone){
         return smsCrondFeignClient.sendSync(phone);
    }

    /**
     * 异步发送
     */
    @RequestMapping("send/async")
    public Response sendAsync(@RequestParam(name = "phone") String phone) {

        return smsCrondFeignClient.sendAsync(phone);

    }

}

