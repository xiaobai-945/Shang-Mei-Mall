package red.mlz.app.domain.goods;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;

@Data
@Accessors(chain = true)
public class CategoryVo {

    private BigInteger id;

    private String name;

    private String image;
}
