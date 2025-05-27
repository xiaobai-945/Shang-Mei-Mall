package red.mlz.console.controller.user;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import red.mlz.common.module.user.entity.User;
import red.mlz.common.utils.BaseUtils;
import red.mlz.common.utils.IpUtils;
import red.mlz.console.module.user.service.BaseUserService;
import javax.servlet.http.HttpServletRequest;


@RestController
@Slf4j
public class ConsoleUserController {
    @Autowired
    BaseUserService baseUserService;


    @RequestMapping("/user/login/web")
    public boolean loginWeb(
                             @RequestParam(name = "phone") String phone,
                             @RequestParam(name = "password") String password) {


        boolean result;

        result = baseUserService.login(phone, password);

        return result;
    }
    @RequestMapping("/user/login/remember")
    public boolean loginRemember(
                                     @RequestParam(name = "phone") String phone,
                                     @RequestParam(name = "password") String password) {
        boolean result;
        result = baseUserService.login(phone, "86", password, false, false, 0);
        return result;

    }

    @RequestMapping("user/login/phone")
    public User loginGetPhone(@RequestParam(name = "phone") String phone) {
        User user = baseUserService.getByPhone(phone);

        return user;
    }

    @RequestMapping("user/login/web/token1")
    public void loginGetToken1(@RequestParam(name = "phone") String phone) {
        User user = baseUserService.getByPhone(phone);
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        baseUserService.refreshUserLoginContext(user.getId(), IpUtils.getIpAddress(request), BaseUtils.currentSeconds());

    }
}
