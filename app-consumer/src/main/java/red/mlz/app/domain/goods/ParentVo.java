package red.mlz.app.domain.goods;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ParentVo {

    private List<ParentCategoryVo> parentList;
}
