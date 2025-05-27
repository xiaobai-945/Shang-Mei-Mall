package red.mlz.app.api.sms;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import red.mlz.common.utils.Response;

@FeignClient(name="app-provider")
public interface SmsCrondFeignClient {


    /**
     * 多线程发送
     */
    @RequestMapping("send/thread")
    Response sendThread(@RequestParam(name = "phoneNumbers") String phoneNumbers);

    /**
     * 同步发送
     */
    @RequestMapping("send/sync")
    Response sendSync(@RequestParam(name = "phone") String phone);

    /**
     * 异步发送
     */
    @RequestMapping("send/async")
    Response sendAsync(@RequestParam(name = "phone") String phone);

}

