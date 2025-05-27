package red.mlz.app.crond;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import red.mlz.app.module.sms_crond.mapper.SmsCrondMapper;
import red.mlz.common.module.sms_crond.entity.SmsCrond;

import red.mlz.common.utils.SmsUtil;

import java.util.List;

@Slf4j
@Component
public class SendCrond {
    @Autowired
    private SmsCrondMapper mapper;

    // 定时任务：每分钟检查待发送的任务
    @Scheduled(fixedDelay = 60000)  // 每分钟执行一次
    public void sendScheduledSms()  {
        // 获取待发送的任务列表
        List<SmsCrond> smsTasks = mapper.getAll();
        // 如果没有待发送的任务，直接返回
        if (smsTasks == null || smsTasks.isEmpty()) {
            log.info("No tasks to send.");
        }

        // 遍历任务列表，发送短信
        for (SmsCrond smsTask : smsTasks) {

            // 将任务状态更新为100（锁定状态）
            int lockResult = mapper.lockTask(smsTask.getId());
            if (lockResult == 0) {
                // 如果更新失败，说明任务已被其他线程锁定，跳过当前任务
                log.warn("Task with ID {} is already being processed by another thread. Skipping.", smsTask.getId());
                continue;
            }

            // 如果任务没有手机号，跳过
            if (smsTask.getPhone() == null) {
                log.error("SmsTask is null or phone number is missing, skipping task.");
                continue;
            }

            // 随机生成验证码
            String phone = smsTask.getPhone();
            String code = (int) (Math.random() * 900000)+"";  // 随机生成验证码

            // 发送短信
            Boolean result = SmsUtil.sendCheckCode(phone, code);
            // 更新任务状态和发送结果
            smsTask.setPhone(phone);
            smsTask.setStatus(result ? 1 : 0);  // 1表示已发送，0表示发送失败
            smsTask.setContent(code);
            smsTask.setSendTime((int) (System.currentTimeMillis() / 1000));
            smsTask.setResult(result ? "短信发送成功" : "短信发送失败");
            smsTask.setUpdatedTime((int) (System.currentTimeMillis() / 1000));  // 更新时间戳
            mapper.update(smsTask);  // 更新任务记录
        }
    }
}
