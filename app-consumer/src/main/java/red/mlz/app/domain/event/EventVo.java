package red.mlz.app.domain.event;

import lombok.Data;

import java.util.List;

@Data
public class EventVo {
    private List<EventItemVo> eventListVo;

}
