package red.mlz.common.utils;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

public class CodeGenerator {
    public static void main(String[] args) {

        // 使用 FastAutoGenerator 快速配置代码生成器
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/xiaobai_Mall?serverTimezone=GMT%2B8", "root", "Aa258369")
                .globalConfig(builder -> {
                    builder.author("小白-945") // 设置作者
                            .outputDir("/Users/liuzefeng/Documents/JavaWeb/mlz-red-server/module/src/main/java/red/mlz/module/module"); // 输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("channel") // 设置父包名
                            .controller("controller")
                            .service("service")
                            .entity("entity") // 设置实体类包名
                            .mapper("mapper") // 设置 Mapper 接口包名
                            .xml("mappers"); // 设置 Mapper XML 文件包名

                })
                .strategyConfig(builder -> {
                    builder.addInclude("channel") // 设置需要生成的表名
                            .entityBuilder()
                            .enableLombok() // 启用 Lombok
                            .enableTableFieldAnnotation() // 启用字段注解
                            .controllerBuilder()
                            .enableRestStyle(); // 启用 REST 风格
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用 Freemarker 模板引擎
                .execute(); // 执行生成

        //配置模版
        TemplateConfig templateConfig = new TemplateConfig.Builder()
                .disable(TemplateType.ENTITY)
                .entity("/templates/entity.java")
                .mapper("/templates/mapper.java")
                .mapper("/templates/mapper.xml")
                .controller("/templates/controller.java")
                .build();

    }
}