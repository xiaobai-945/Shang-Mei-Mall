package red.mlz.app.module.sms_crond.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import red.mlz.app.module.sms_crond.mapper.SmsCrondMapper;
import red.mlz.app.module.sms_crond.mapper.SmsForbidMapper;
import red.mlz.app.module.sms_crond.mapper.SmsUseMapper;
import red.mlz.app.mq.RabbitMQConfig;
import red.mlz.common.module.sms_crond.entity.SmsCrond;
import red.mlz.common.module.sms_crond.entity.SmsForbid;
import red.mlz.common.module.sms_crond.entity.SmsUse;
import red.mlz.common.utils.BaseUtils;
import red.mlz.common.utils.SmsUtil;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class SmsCrondService {

    @Autowired
    private SmsCrondMapper smsCrondMapper;

    @Autowired
    private SmsForbidMapper smsForbidMapper;

    @Autowired
    private SmsUseMapper smsUseMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // 获取发送任务列表
    public List<SmsCrond> getAll() {
        return smsCrondMapper.getAll();
    }

    // 获取id信息
    public SmsCrond getById(BigInteger id) {
        return smsCrondMapper.getById(id);
    }

    // 获取id信息
    public SmsCrond extractById(BigInteger id) {
        return smsCrondMapper.extractById(id);
    }

    // 记录发送信息
    private SmsCrond createSmsTask(String phone, String code) {
        Boolean result = SmsUtil.sendCheckCode(phone, code); // 使用自定义的 SmsUtils 发送短信
        // 获取当前时间戳作为发送时间
        int sendTime = (int) (System.currentTimeMillis() / 1000);
        SmsCrond smsTask = new SmsCrond();
        smsTask.setPhone(phone);
        smsTask.setStatus(result ? 1 : 2);  // 设置状态为已发送或发送失败
        smsTask.setContent(code);  // 记录验证码内容
        smsTask.setResult(result ? "短信发送成功" : "短信发送失败");  // 记录发送结果
        smsTask.setSendTime(sendTime);  // 当前时间戳，作为发送时间
        smsTask.setCreatedTime(BaseUtils.currentSeconds());  // 创建时间
        smsTask.setUpdatedTime(BaseUtils.currentSeconds());  // 更新时间
        smsTask.setIsDeleted(0);  // 默认未删除
        smsCrondMapper.insert(smsTask);
        return smsTask;
    }


    // 创建一个线程池
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    // 多线程发送短信
    public int sendThread(String phoneNumber) {
        int count = 0; // 记录成功发送

        String[] phoneNumbers = phoneNumber.split("\\$");
        // 遍历手机号列表，提交给线程池
        for (String phone : phoneNumbers) {
            if (isForbidden(phone)) {
                System.out.println("手机号 " + phone + " 被禁止发送短信");
                continue;
            }
            if (isExceedFrequency(phone)) {
                System.out.println("手机号 " + phone + " 发送频率过高");
                continue;
            }
            executorService.submit(() -> {
                // 生成验证码
                String code = (int) (Math.random() * 900000) + "";  // 随机生成验证码
                // 创建SmsCrond任务
                SmsCrond smsTask = createSmsTask(phone, code);
                // 发送短信任务到MQ
                rabbitTemplate.convertAndSend(RabbitMQConfig.SMS_EXCHANGE, RabbitMQConfig.SMS_QUEUE, smsTask);

                // 记录发送次数
                recordSmsUse(phone);
            });
            count++;
        }
        // 提交完所有任务后，关闭线程池
        executorService.shutdown();
        return count;
    }


    // 记录一个新的发送任务（异步发送）
    public int sendAsync(String phone) {
        if (isForbidden(phone)) {
            return -1; // 表示被禁止发送
        }
        if (isExceedFrequency(phone)) {
            return -2; // 表示发送频率过高
        }

        SmsCrond smsTask = new SmsCrond();
        smsTask.setPhone(phone);
        smsTask.setSendTime(BaseUtils.currentSeconds());  // 当前时间戳，作为发送时间
        smsTask.setStatus(0);  // 设置状态为待发送
        smsTask.setResult("未发送");  // 初始结果为空
        smsTask.setCreatedTime(BaseUtils.currentSeconds());  // 创建时间
        smsTask.setUpdatedTime(BaseUtils.currentSeconds());  // 更新时间
        smsTask.setIsDeleted(0);  // 默认未删除



        // 插入到数据库
        int result = smsCrondMapper.insert(smsTask);

        // 记录发送次数
        recordSmsUse(phone);

        return result;
    }

    // 同步发送短信并记录结果
    public int sendSmsSync(String phone) {
        if (isForbidden(phone)) {
            return -1; // 表示被禁止发送
        }
        if (isExceedFrequency(phone)) {
            return -2; // 表示发送频率过高
        }

        // 获取当前时间戳作为发送时间
        int sendTime = (int) (System.currentTimeMillis() / 1000);

        // 生成验证码
        String code = (int) (Math.random() * 900000) + "";  // 随机生成验证码

        // 发送短信
        Boolean result = SmsUtil.sendCheckCode(phone, code); // 使用自定义的 SmsUtils 发送短信

        // 记录发送信息
        // 创建SmsCrond任务
        SmsCrond smsTask = createSmsTask(phone, code);
        // 发送短信任务到MQ
        rabbitTemplate.convertAndSend(RabbitMQConfig.SMS_EXCHANGE, RabbitMQConfig.SMS_QUEUE, smsTask);
        // 插入到数据库
        int insertResult = smsCrondMapper.insert(smsTask);

        // 记录发送次数
        recordSmsUse(phone);

        return insertResult;
    }

    // 检查是否被禁止发送短信
    private boolean isForbidden(String phone) {
        SmsForbid smsForbid = smsForbidMapper.getById(new BigInteger(phone));
        if (smsForbid != null) {
            int currentTime = (int) (LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) / 60);
            if (currentTime >= smsForbid.getBeginTime() && currentTime <= smsForbid.getEndTime()) {
                return true; // 当前时间在禁止范围内
            }
        }
        return false;
    }

    // 检查是否超出发送频率限制
    private boolean isExceedFrequency(String phone) {
        LocalDateTime now = LocalDateTime.now();
        SmsUse smsUse = smsUseMapper.extractById(new BigInteger(phone));
        if (smsUse == null) {
            // 如果没有记录，初始化记录
            smsUse = new SmsUse();
            smsUse.setPhone(phone);
            smsUse.setYear(now.getYear());
            smsUse.setMonth(now.getMonthValue());
            smsUse.setDay(now.getDayOfMonth());
            smsUse.setHour(now.getHour());
            smsUse.setMinute(now.getMinute());
            smsUse.setCount(0);
            smsUse.setCreateTime((int) (now.toEpochSecond(ZoneOffset.UTC)));
            smsUse.setUpdateTime((int) (now.toEpochSecond(ZoneOffset.UTC)));
            smsUseMapper.insert(smsUse);
            return false;
        } else {
            // 检查是否超出频率限制（每分钟最多5条）
            if (smsUse.getCount() >= 5) {
                // 超出频率限制，加入禁止表
                int forbidDuration = 60 * 60; // 禁止1小时
                SmsForbid smsForbid = new SmsForbid();
                smsForbid.setPhone(phone);
                smsForbid.setBeginTime((int) (System.currentTimeMillis() / 1000));
                smsForbid.setEndTime((int) (System.currentTimeMillis() / 1000) + forbidDuration);
                smsForbid.setCreateTime((int) (System.currentTimeMillis() / 1000));
                smsForbid.setUpdateTime((int) (System.currentTimeMillis() / 1000));
                smsForbidMapper.insert(smsForbid);
                return true; // 超出频率限制
            }
            // 更新发送次数
            smsUse.setCount(smsUse.getCount() + 1);
            smsUse.setUpdateTime((int) (now.toEpochSecond(ZoneOffset.UTC)));
            smsUseMapper.update(smsUse);
            return false; // 未超出频率限制
        }

    }

    // 记录短信发送次数
    private void recordSmsUse(String phone) {
        LocalDateTime now = LocalDateTime.now();
        SmsUse smsUse = smsUseMapper.extractById(new BigInteger(phone));
        if (smsUse == null) {
            // 如果没有记录，初始化记录
            smsUse = new SmsUse();
            smsUse.setPhone(phone);
            smsUse.setYear(now.getYear());
            smsUse.setMonth(now.getMonthValue());
            smsUse.setDay(now.getDayOfMonth());
            smsUse.setHour(now.getHour());
            smsUse.setMinute(now.getMinute());
            smsUse.setCount(1);
            smsUse.setCreateTime((int) (now.toEpochSecond(ZoneOffset.UTC)));
            smsUse.setUpdateTime((int) (now.toEpochSecond(ZoneOffset.UTC)));
            smsUseMapper.insert(smsUse);
        } else {
            // 更新发送次数
            smsUse.setCount(smsUse.getCount() + 1);
            smsUse.setUpdateTime((int) (now.toEpochSecond(ZoneOffset.UTC)));
            smsUseMapper.update(smsUse);
        }
    }
}