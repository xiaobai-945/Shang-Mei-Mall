package red.mlz.console.api.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import red.mlz.common.module.user.entity.User;

import java.math.BigInteger;


@FeignClient(name="consoler-provider")
public interface ConsoleUserFeignClient {


    @RequestMapping("/user/login/web")
    boolean loginWeb(
            @RequestParam(name = "phone") String phone,
            @RequestParam(name = "password") String password);

    @RequestMapping("/user/login/remember")
    boolean loginRemember(
            @RequestParam(name = "phone") String phone,
            @RequestParam(name = "countryCode",required = false) String countryCode,
            @RequestParam(name = "noPasswd",required = false) String noPasswd,
            @RequestParam(name = "password",required = false) boolean password,
            @RequestParam(name = "remember") boolean remember,
            @RequestParam(name = "lifeTime",required = false) Integer lifeTime);

    @RequestMapping("user/login/phone")
    User loginGetPhone(@RequestParam(name = "phone") String phone);

    @RequestMapping("user/login/web/token")
    void loginGetToken(@RequestParam(value = "id") BigInteger id,@RequestParam(value = "ipAddress")  String ipAddress,@RequestParam(value = "i")  int i);

}
