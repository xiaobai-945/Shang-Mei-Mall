package red.mlz.console.domain.goods;

import lombok.Data;
import lombok.experimental.Accessors;
import red.mlz.console.domain.BaseContentValueVo;

import java.util.List;
@Data
@Accessors(chain = true)
public class GoodsInfoVo {

    private List<String> goodsImages;

    private Integer price;

    private Integer sales;

    private String goodsName;

    private Integer sevenDayReturn;

    private List<BaseContentValueVo> content;

    private String createdTime;

    private String updatedTime;
}
