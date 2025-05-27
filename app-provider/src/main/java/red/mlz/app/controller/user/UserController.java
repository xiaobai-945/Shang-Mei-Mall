package red.mlz.app.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import red.mlz.app.annotations.VerifiedUser;
import red.mlz.app.module.user.service.BaseUserService;
import red.mlz.common.module.user.entity.User;
import red.mlz.common.utils.BaseUtils;
import red.mlz.common.utils.IpUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;


@RestController
@Slf4j
public class UserController {

    @Autowired
    private BaseUserService baseUserService;


    @RequestMapping("/user/login/app")
    public boolean loginApp(@VerifiedUser User loginUser,
                            @RequestParam(name = "phone") String phone,
                            @RequestParam(name = "password") String password) {

        //合法用户直接登录
        boolean result = baseUserService.login(phone, password);
        return result;


    }

    @RequestMapping("user/phone")
    public User getByPhone(@RequestParam(name = "phone") String phone) {

        User user = baseUserService.getByPhone(phone);

        return user;
    }

    @RequestMapping("user/login/context")
    public void refreshUserLoginContext(@RequestParam(name = "phone") String phone) {
        User user = baseUserService.getByPhone(phone);
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        baseUserService.refreshUserLoginContext(user.getId(), IpUtils.getIpAddress(request), BaseUtils.currentSeconds());

    }


    @RequestMapping("/user/register/app")
    public User extractByPhone(User loginUser,
                               @RequestParam(name = "phone") String phone,
                               @RequestParam(name = "gender") Integer gender,
                               @RequestParam(name = "avatar", required = false) String avatar,
                               @RequestParam(name = "name") String name,
                               @RequestParam(name = "password") String password,
                               @RequestParam(name = "country", required = false) String country,
                               @RequestParam(name = "province", required = false) String province,
                               @RequestParam(name = "city", required = false) String city) {

        User user = baseUserService.extractByPhone(phone, "86");
        return user;
    }

    @RequestMapping("/login/context")
    public void refreshUserLoginContext(@RequestParam(name = "phone") String phone,
                                        @RequestParam(name = "country", required = false) String country) {

        User user = baseUserService.extractByPhone(phone, country);

        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        baseUserService.refreshUserLoginContext(user.getId(), IpUtils.getIpAddress(request), BaseUtils.currentSeconds());
    }


    @RequestMapping("/register")
    public BigInteger registerUser(
            @RequestParam(name = "phone") String phone,
            @RequestParam(name = "gender") Integer gender,
            @RequestParam(name = "avatar", required = false) String avatar,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "country", required = false) String country,
            @RequestParam(name = "province", required = false) String province,
            @RequestParam(name = "city", required = false) String city) {

        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        BigInteger newUserId = baseUserService.registerUser(name, phone, gender, avatar, password, country, province, city, IpUtils.getIpAddress(request));
        return newUserId;

    }

    @RequestMapping("user/userId")
    public User getById(BigInteger id) {
        User user = baseUserService.getById(id);
        return user;

    }

}
