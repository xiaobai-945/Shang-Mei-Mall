package red.mlz.app.api.user;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import red.mlz.common.module.user.entity.User;

import java.math.BigInteger;


@FeignClient(name="app-provider")
public interface UserFeignClient {


    @RequestMapping("/user/login/app")
     boolean loginApp(
                      @RequestParam(name = "phone") String phone,
                      @RequestParam(name = "password") String password);

    @RequestMapping("user/phone")
    User getByPhone(@RequestParam(name = "phone") String phone);

    @RequestMapping("user/login/context")
    void refreshUserLoginContext(@RequestParam(name = "id") BigInteger id, @RequestParam(name = "phone") String phone, @RequestParam(name = "i") int i);


    @RequestMapping("/user/register/app")
    User extractByPhone(
                               @RequestParam(name = "phone") String phone,
                               @RequestParam(name = "country", required = false) String country);

    @RequestMapping("/login/context")
    void refreshUserLoginContext(@RequestParam(name = "phone") String phone,
                                        @RequestParam(name = "country", required = false) String country);

    @RequestMapping("/register")
    BigInteger registerUser(
            @RequestParam(name = "phone") String phone,
            String s, @RequestParam(name = "gender") Integer gender,
            @RequestParam(name = "avatar", required = false) String avatar,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "country", required = false) String country,
            @RequestParam(name = "province", required = false) String province,
            @RequestParam(name = "city", required = false) String city);

    @RequestMapping("user/userId")
    User getById(BigInteger id);
}
