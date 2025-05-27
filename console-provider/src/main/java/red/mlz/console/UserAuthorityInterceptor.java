package red.mlz.console;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import red.mlz.common.module.user.entity.User;

import red.mlz.common.utils.BaseUtils;
import red.mlz.common.utils.Response;
import red.mlz.common.utils.SignUtils;
import red.mlz.common.utils.SpringUtils;
import red.mlz.console.module.user.service.BaseUserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigInteger;

@Slf4j
public class UserAuthorityInterceptor implements HandlerInterceptor {

    @Resource
    private BaseUserService userService;
    private boolean isCheckAuthority;

    public UserAuthorityInterceptor(String[] arguments) {
        if (arguments == null || arguments.length <= 3) {
            isCheckAuthority = true;
            return;
        }

        String isMockUserLogin = arguments[2];
        if (BaseUtils.isEmpty(isMockUserLogin)) {
            isCheckAuthority = true;
        } else {
            isCheckAuthority = Boolean.parseBoolean(isMockUserLogin);
        }
        log.info("Check user authority: {}", Boolean.toString(isCheckAuthority));
    }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

    // 设置响应编码为 UTF-8，避免中文乱码
    response.setContentType("application/json; charset=UTF-8");

    if (isCheckAuthority) {
      String isAppS = SpringUtils.getProperty("application.isapp");
      boolean isApp = isAppS.equals("1");

      if (isApp) {
        // 处理 App 请求：从请求头中获取 sign
        String signKey = SpringUtils.getProperty("application.sign.key");
        String sign = request.getHeader(signKey);
        if (!BaseUtils.isEmpty(sign)) {
          BigInteger userId = SignUtils.parseSign(sign);
          log.info("userId: {}, sign: {}", userId, sign);
          if (!BaseUtils.isEmpty(userId)) {
            User user = userService.getById(userId);
            request.setAttribute("user", user); // 将用户信息放入请求属性

            // 返回 200 OK 并携带用户信息（登录成功）
            response.setStatus(200);
            response.getWriter().write(JSON.toJSONString(new Response<>(1001, user)));  // 登录成功
            return true;
          }
        }

        // 如果 sign 无效或未登录，返回 200 OK 和登录失败信息
        response.setStatus(200);  // 返回 200 OK
        response.getWriter().write(JSON.toJSONString(new Response(1002)));  // 登录失败
        return false;
      } else {
        // 处理 Web 请求：从 session 中获取用户信息
        HttpSession session = request.getSession(false);
        if (BaseUtils.isEmpty(session)) {
          response.setStatus(200);  // 返回 200 OK
          response.getWriter().write(JSON.toJSONString(new Response(1002)));  // 登录失败
          return false;
        }

        String signKey = SpringUtils.getProperty("application.session.key");
        Object value = session.getAttribute(signKey);
        if (value == null) {
          response.setStatus(200);  // 返回 200 OK
          response.getWriter().write(JSON.toJSONString(new Response(1002)));  // 登录失败
          return false;
        }

        String sValue = (String) value;
        User user = JSON.parseObject(sValue, User.class);
        if (user != null) {
          request.setAttribute("user", user); // 将用户信息放入请求属性

          // 返回 200 OK 并携带用户信息（登录成功）
          response.setStatus(200);  // 返回 200 OK
          response.getWriter().write(JSON.toJSONString(new Response<>(1001, user)));  // 登录成功
          return true;
        }
      }

      // 如果未验证通过，返回 200 OK 和其他错误信息
      response.setStatus(200);  // 返回 200 OK
      response.getWriter().write(JSON.toJSONString(new Response(4004)));  // 网络繁忙或其他错误
      return false;
    }

    // 如果不需要验证，放行
    response.setStatus(200);  // 返回 200 OK
    return true;
  }




}
