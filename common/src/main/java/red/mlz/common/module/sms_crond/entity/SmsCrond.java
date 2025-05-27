package red.mlz.common.module.sms_crond.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;


@Data
@Accessors(chain = true)
public class SmsCrond {
    //
    private BigInteger id;
    //用户手机号码
    private String phone;
    //发送时间
    private Integer sendTime;
    //短信内容
    private String content;
    //发送状态
    private Integer status;
    //发送结果
    private String result;
    //创建时间
    private Integer createdTime;
    //更新时间
    private Integer updatedTime;
    //删除
    private Integer isDeleted;
}