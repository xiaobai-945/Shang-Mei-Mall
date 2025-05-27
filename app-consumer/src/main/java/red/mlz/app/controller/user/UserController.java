package red.mlz.app.controller.user;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import red.mlz.app.UserDefine;
import red.mlz.app.annotations.VerifiedUser;
import red.mlz.app.api.user.UserFeignClient;
import red.mlz.app.domain.user.UserInfoVo;
import red.mlz.app.domain.user.UserLoginInfoVo;
import red.mlz.common.module.user.entity.User;
import red.mlz.common.utils.BaseUtils;
import red.mlz.common.utils.IpUtils;
import red.mlz.common.utils.Response;
import red.mlz.common.utils.SignUtils;
import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;

@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserFeignClient userFeignClient;


    @RequestMapping("/user/login/app")
    public Response loginApp(@VerifiedUser User loginUser,
                             @RequestParam(name = "phone") String phone,
                             @RequestParam(name = "password") String password) {
        if (!BaseUtils.isEmpty(loginUser)) {
            return new Response(4004);
        }
        //合法用户直接登录
        boolean result = userFeignClient.loginApp(phone, password);
        if (!result) {
            return new Response(4004);
        }
        User user = userFeignClient.getByPhone(phone);

        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        userFeignClient.refreshUserLoginContext(user.getId(), IpUtils.getIpAddress(request), BaseUtils.currentSeconds());

        UserInfoVo userInfo = new UserInfoVo();
        userInfo.setGender(user.getGender());
        userInfo.setName(user.getUsername());
        userInfo.setPhone(user.getPhone());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setUserId(user.getId());

        UserLoginInfoVo loginInfo = new UserLoginInfoVo();
        loginInfo.setSign(SignUtils.makeSign(user.getId()));

        loginInfo.setUserInfo(userInfo);
        return new Response(1001, loginInfo);
    }

    @RequestMapping("/user/register/app")
    public Response registerApp(@VerifiedUser User loginUser,
                                @RequestParam(name = "phone") String phone,
                                @RequestParam(name = "gender") Integer gender,
                                @RequestParam(name = "avatar", required = false) String avatar,
                                @RequestParam(name = "name") String name,
                                @RequestParam(name = "password") String password,
                                @RequestParam(name = "country", required = false) String country,
                                @RequestParam(name = "province", required = false) String province,
                                @RequestParam(name = "city", required = false) String city) {
        if (!BaseUtils.isEmpty(loginUser)) {
            return new Response(4004);
        }


        //考虑用户已经注册了
        //即phone存在
        //直接按照登录处理，返回sign
        User user = userFeignClient.extractByPhone(phone,"86");
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        BigInteger newUserId;
        if(!BaseUtils.isEmpty(user)){
            //如果用户被禁止登录
            if(user.getIsDeleted().equals(1) || user.getIsBan().equals(1)){
                return new Response(1010);
            }
            newUserId = user.getId();
            userFeignClient.refreshUserLoginContext(user.getId(), IpUtils.getIpAddress(request), BaseUtils.currentSeconds());
        }else {
            //注册新用户
            if (!UserDefine.isGender(gender)) {
                return new Response(2014);
            }
            try {
                newUserId = userFeignClient.registerUser(name, phone, gender, avatar, password,country, province, city, IpUtils.getIpAddress(request));
            } catch (Exception exception) {
                return new Response(4004);
            }

        }
        user = userFeignClient.getById(newUserId);

        UserInfoVo userInfo = new UserInfoVo();
        userInfo.setGender(user.getGender());
        userInfo.setName(user.getUsername());
        userInfo.setPhone(user.getPhone());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setUserId(user.getId());

        UserLoginInfoVo loginInfo = new UserLoginInfoVo();
        loginInfo.setSign(SignUtils.makeSign(user.getId()));

        loginInfo.setUserInfo(userInfo);
        return new Response(1001, loginInfo);
    }
}
