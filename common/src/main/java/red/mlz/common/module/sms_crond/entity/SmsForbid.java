package red.mlz.common.module.sms_crond.entity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;


@Data
@Accessors(chain = true)
public class SmsForbid {
    //⾃增id
    private BigInteger id;
    //⼿机号
    private String phone;
    //开始时间
    private Integer beginTime;
    //结束时间
    private Integer endTime;
    //创建时间
    private Integer createTime;
    //修改时间
    private Integer updateTime;
}