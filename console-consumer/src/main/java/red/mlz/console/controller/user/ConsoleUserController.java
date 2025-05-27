package red.mlz.console.controller.user;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import red.mlz.common.module.user.entity.User;
import red.mlz.common.utils.BaseUtils;
import red.mlz.common.utils.IpUtils;
import red.mlz.common.utils.Response;
import red.mlz.common.utils.SpringUtils;
import red.mlz.console.api.user.ConsoleUserFeignClient;
import red.mlz.console.domain.user.UserInfoVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@RestController
@Slf4j
public class ConsoleUserController {
    @Resource
    private ConsoleUserFeignClient consoleUserFeignClient;


    @RequestMapping("/user/login/web")
    public Response loginWeb(
            HttpSession httpSession,
            @RequestParam(name = "phone") String phone,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "remember") boolean remember) {


        boolean result;
        if (remember) {
            result = consoleUserFeignClient.loginWeb(phone, password);
        } else {
            result = consoleUserFeignClient.loginRemember(phone, "86", password,false, false, 0);
        }
        if (!result) {
            return new Response(1010);
        }

        User user = consoleUserFeignClient.loginGetPhone(phone);
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        consoleUserFeignClient.loginGetToken(user.getId(), IpUtils.getIpAddress(request), BaseUtils.currentSeconds());

        UserInfoVo userInfo = new UserInfoVo();
        userInfo.setUserGender(user.getGender());
        userInfo.setUserName(user.getUsername());
        userInfo.setUserPhone(user.getPhone());
        userInfo.setUserAvatar(user.getAvatar());

        // 写session
        httpSession.setAttribute(SpringUtils.getProperty("application.session.key"), JSON.toJSONString(user));

        return new Response(1001, userInfo);
    }
}
