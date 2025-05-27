package red.mlz.app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import red.mlz.common.config.dynamic.datasource.EnableDataSource;

@EnableScheduling
@SpringBootApplication(scanBasePackages={"red.mlz","red.mlz.common.*"})
@MapperScan({"red.mlz.app.module.*.mapper","red.mlz.common.*"})
@EnableDataSource
@EnableEurekaClient
public class AppProvider {

    public static void main(String[] args) {
        SpringApplication.run(AppProvider.class, args);
    }

}
