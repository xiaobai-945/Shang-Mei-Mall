package red.mlz.common.config.dynamic.datasource.constant;

import cn.hutool.core.collection.CollUtil;

import java.util.List;

public class DataSourceName {

    /**
     * 默认
     */
    public final static String MASTER = "master";


    /**
     * 多数据源
     */
    public final static String SECONDARY = "secondary";

    public final static List<String> DATA_SOURCE_NAMES = CollUtil.newArrayList(MASTER);
}
