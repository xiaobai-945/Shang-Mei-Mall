package red.mlz.console.config;


import com.alibaba.druid.support.http.StatViewServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DruidConfig {

    @Bean
    public ServletRegistrationBean<StatViewServlet> statViewServlet() {
        ServletRegistrationBean<StatViewServlet> servletRegistrationBean = new ServletRegistrationBean<>(
                new StatViewServlet(), "/druid/*");  // 设置控制台路径
        servletRegistrationBean.addInitParameter("allow", "127.0.0.1");  // 允许访问的 IP 地址
        servletRegistrationBean.addInitParameter("deny", "");  // 拒绝其他 IP 地址
        servletRegistrationBean.addInitParameter("loginUsername", "admin");  // 设置控制台用户名
        servletRegistrationBean.addInitParameter("loginPassword", "admin123");  // 设置控制台密码
        return servletRegistrationBean;
    }
}
