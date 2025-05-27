
package red.mlz.common.config.dynamic.datasource.config;


import org.springframework.boot.jdbc.DataSourceBuilder;
import red.mlz.common.config.dynamic.datasource.properties.DataSourceProperties;

import javax.sql.DataSource;

/**
 * DataSource
 *
 */
public class DynamicDataSourceFactory {

    public static DataSource buildDruidDataSource(DataSourceProperties properties) {

        return DataSourceBuilder.create()
                .driverClassName(properties.getDriverClassName())
                .url(properties.getJdbcUrl())
                .username(properties.getUsername())
                .password(properties.getPassword())
                .build();
    }


}