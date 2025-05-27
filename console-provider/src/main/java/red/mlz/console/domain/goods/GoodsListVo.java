package red.mlz.console.domain.goods;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;

@Data
@Accessors(chain = true)
public class GoodsListVo {

    private BigInteger id;

    private String title;

    private String goodImage;

    private Integer sales;

    private Integer price;

//    private List<Tag> tags;


}
