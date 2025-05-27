package red.mlz.common.utils;

import java.util.HashMap;
import java.util.Map;

public class ResponseCode {
    private static final Map<Integer, String> statusMap = new HashMap<Integer, String>();

    static {
        statusMap.put(1001, "OK");
        statusMap.put(1002, "没有登录哦~");
        statusMap.put(1003, "账号密码不匹配或账号不存在");
        statusMap.put(1004, "登录失败");



        //create user and forget password
        statusMap.put(2013, "已登录，不能进行注册");
        statusMap.put(2014, "账号尚未注册");
        statusMap.put(2015, "注册失败");

        // create and update goods
        statusMap.put(3001,"商品新增失败");
        statusMap.put(3002,"商品更新失败");

        statusMap.put(3002,"商品更新失败");
        statusMap.put(3002,"商品更新失败");

        // delete error
        statusMap.put(3003,"删除失败");

        //console error
        //goods error
        statusMap.put(3051, "商品必填信息不能为空");
        statusMap.put(3052, "商品类目ID不正确");



        statusMap.put(401, "系统错误");

        statusMap.put(4003, "权限不足");
        statusMap.put(4004, "网络繁忙");
        statusMap.put(4005, "操作失败");
        statusMap.put(4006,"内容为空");

    }

    public static String getMsg(Integer code) {
        return statusMap.get(code);
    }
}
