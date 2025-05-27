package red.mlz.app.domain.banner;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class BannerVo {
    private List<BannerItemVo> bannerList;

}
