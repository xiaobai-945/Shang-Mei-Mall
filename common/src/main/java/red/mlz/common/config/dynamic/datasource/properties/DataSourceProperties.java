/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package red.mlz.common.config.dynamic.datasource.properties;

import lombok.Data;

/**
 * 多数据源属性
 */
@Data
public class DataSourceProperties {
    private String driverClassName;
    private String jdbcUrl;
    private String username;
    private String password;

}