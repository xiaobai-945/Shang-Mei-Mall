package red.mlz.app.module.event.service;


import org.springframework.stereotype.Service;
import red.mlz.app.module.event.mapper.EventMapper;
import red.mlz.common.config.ReadOnly;
import red.mlz.common.module.event.entity.Event;


import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.List;

@Service
public class EventService {
    @Resource
    private EventMapper eventMapper;

    // 获取类目列表
    @ReadOnly
    public List<Event> getAll() {
        return eventMapper.getAll();
    }

    // 根据ID查询操作
    @ReadOnly
    public Event getById(BigInteger id) {
        return eventMapper.getById(id);
    }

    // 根据ID提取操作
    @ReadOnly
    public Event extractById(BigInteger id) {
        return eventMapper.extractById(id);
    }

    // 插入操作
    public int insert(Event event) {
        return eventMapper.insert(event);
    }

    // 更新操作
    public int update(Event event) {
        return eventMapper.update(event);
    }

    // 删除操作
    public int delete(BigInteger id, Integer time) {
        return eventMapper.delete(id, time);
    }
}
