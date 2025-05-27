package red.mlz.console.module.channel.service;


import org.springframework.stereotype.Service;
import red.mlz.common.config.ReadOnly;
import red.mlz.common.module.channel.entity.Channel;
import red.mlz.console.module.channel.mapper.ChannelMapper;


import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.List;

@Service
public class ChannelService {
    @Resource
    private ChannelMapper channelMapper;

    // 获取类目列表
    @ReadOnly
    public List<Channel> getAll() {
        return channelMapper.getAll();
    }

    // 根据ID查询操作
    @ReadOnly
    public Channel getById(BigInteger id) {
        return channelMapper.getById(id);
    }

    // 根据ID提取操作
    @ReadOnly
    public Channel extractById(BigInteger id) {
        return channelMapper.extractById(id);
    }

    // 插入操作
    public int insert(Channel channel) {
        return channelMapper.insert(channel);
    }

    // 更新操作
    public int update(Channel channel) {
        return channelMapper.update(channel);
    }

    // 删除操作
    public int delete(BigInteger id, Integer time) {
        return channelMapper.delete(id, time);
    }
}
