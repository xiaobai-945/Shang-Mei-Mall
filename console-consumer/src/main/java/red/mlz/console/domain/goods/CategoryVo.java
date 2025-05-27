package red.mlz.console.domain.goods;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class CategoryVo {

    private BigInteger id;

    private String name;

    private List<CategoryVo> children = new ArrayList<>();

}
