package red.mlz.app.domain.goods;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;
import java.util.List;

@Data
@Accessors(chain = true)
public class ParentCategoryVo {

    private BigInteger id;

    private String name;

    private String image;

    private List<CategoryItemVo> categoryList;

}
