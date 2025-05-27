package red.mlz.console;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import red.mlz.common.config.dynamic.datasource.EnableDataSource;

@SpringBootApplication(scanBasePackages={"red.mlz","red.mlz.common.*"})
@MapperScan({"red.mlz.console.module.*.mapper","red.mlz.common.*"})
@EnableConfigurationProperties
@EnableDataSource
@EnableEurekaClient
public class ConsoleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsoleApplication.class, args);
    }

}
