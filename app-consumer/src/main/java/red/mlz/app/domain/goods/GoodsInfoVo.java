package red.mlz.app.domain.goods;

import lombok.Data;
import lombok.experimental.Accessors;
import red.mlz.app.domain.BaseContentValueVo;

import java.util.List;

@Data
@Accessors(chain = true)

public class GoodsInfoVo  {

    private String categoryName;

    private String categoryImage;

    private List<String> goodsImages;

    private String source;

    private Integer price;

    private Integer sales;

    private String goodsName;

    private Integer sevenDayReturn;

    private List<String> tags;

    private List<BaseContentValueVo> content;

}
