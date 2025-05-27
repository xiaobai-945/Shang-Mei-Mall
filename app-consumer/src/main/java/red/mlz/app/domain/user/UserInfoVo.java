package red.mlz.app.domain.user;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;

@Data
@Accessors(chain = true)
public class UserInfoVo {
    private BigInteger userId;
    private String name;
    private Integer gender;
    private String phone;
    private String avatar;
}
