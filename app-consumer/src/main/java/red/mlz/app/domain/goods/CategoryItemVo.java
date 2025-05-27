package red.mlz.app.domain.goods;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;

@Data
@Accessors(chain = true)
public class CategoryItemVo {

    private BigInteger id;

    private BigInteger parentId;

    private String name;

    private String image;







}
