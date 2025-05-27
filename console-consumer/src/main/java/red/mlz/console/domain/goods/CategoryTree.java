package red.mlz.console.domain.goods;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
@Data
@Accessors(chain = true)
public class CategoryTree {

    private List<CategoryVo> data;
}
