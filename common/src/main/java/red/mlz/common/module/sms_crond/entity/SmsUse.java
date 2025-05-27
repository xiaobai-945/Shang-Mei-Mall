package red.mlz.common.module.sms_crond.entity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;


@Data
@Accessors(chain = true)
public class SmsUse {
    //⾃增id
    private BigInteger id;
    //⼿机号
    private String phone;
    //年
    private Integer year;
    //⽉
    private Integer month;
    //⽇
    private Integer day;
    //⼩时
    private Integer hour;
    //分
    private Integer minute;
    //发送次数
    private Integer count;
    //创建时间
    private Integer createTime;
    //修改时间
    private Integer updateTime;
}