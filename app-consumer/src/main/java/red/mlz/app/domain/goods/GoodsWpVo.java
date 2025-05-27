package red.mlz.app.domain.goods;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigInteger;

@Data
@Accessors(chain = true)
public class GoodsWpVo implements Serializable {

    public BigInteger categoryId;
    public Integer page;
    public Integer pageSize;
    public String name;


}
