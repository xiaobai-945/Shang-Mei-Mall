

package red.mlz.common.config.dynamic.datasource.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import red.mlz.common.config.dynamic.datasource.aspect.DataSourceAspect;
import red.mlz.common.config.dynamic.datasource.constant.DataSourceName;
import red.mlz.common.config.dynamic.datasource.properties.DataSourceProperties;
import red.mlz.common.config.dynamic.datasource.properties.DynamicDataSourceProperties;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 配置多数据源
 *
 */
@Configuration
@Slf4j
@EnableConfigurationProperties(DynamicDataSourceProperties.class)
public class DynamicDataSourceConfig {
    @Resource
    private DynamicDataSourceProperties properties;

    @Bean
    public DataSourceAspect getDataSourceAspect() {
        return new DataSourceAspect();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public DynamicDataSource dynamicDataSource() {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();

        Map<Object, Object> dataSourceMap = getDynamicDataSource();

        //默认数据源
        dynamicDataSource.setDefaultTargetDataSource(dataSourceMap.get(DataSourceName.MASTER));

        // 配置多数据源
        Map<Object, Object> targetDataSources = DataSourceName.DATA_SOURCE_NAMES
                .stream()
                .collect(Collectors.toMap(k -> k, dataSourceMap::get));
        dynamicDataSource.setTargetDataSources(targetDataSources);

        return dynamicDataSource;
    }

    private Map<Object, Object> getDynamicDataSource() {
        Map<String, DataSourceProperties> dataSourcePropertiesMap = properties.getDatasource();
        Map<Object, Object> targetDataSources = new HashMap<>(dataSourcePropertiesMap.size());
        dataSourcePropertiesMap.forEach((k, v) -> {
            DataSource druidDataSource = DynamicDataSourceFactory.buildDruidDataSource(v);

            log.info("DataSource type: {}", druidDataSource.getClass().getSimpleName());
            targetDataSources.put(k, druidDataSource);
        });

        return targetDataSources;
    }

}
