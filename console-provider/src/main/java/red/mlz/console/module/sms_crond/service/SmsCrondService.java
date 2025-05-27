package red.mlz.console.module.sms_crond.service;

import org.springframework.stereotype.Service;
import red.mlz.common.module.sms_crond.entity.SmsCrond;
import red.mlz.common.utils.BaseUtils;
import red.mlz.common.utils.SmsUtil;
import red.mlz.console.module.sms_crond.mapper.SmsCrondMapper;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class SmsCrondService {
    @Resource
    public SmsCrondMapper mapper;


    // 获取发送任务列表
    public List<SmsCrond> getAll() { return mapper.getAll(); }

    // 获取id信息
    public SmsCrond getById(BigInteger id){ return mapper.getById(id);}

    // 获取id信息
    public SmsCrond extractById(BigInteger id){ return mapper.extractById(id);}


    // 创建一个线程池
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    // 多线程发送短信
    public int sendThread(String phoneNumber) {
        int count = 0; //记录成功发送

        String[] phoneNumbers = phoneNumber.split("\\$");
        // 遍历手机号列表，提交给线程池
        for (String phone : phoneNumbers) {
            executorService.submit(() -> {

                // 生成验证码
                String code =(int) (Math.random() * 900000)+"";  // 随机生成验证码

                // 发送短信
                Boolean result = SmsUtil.sendCheckCode(phone, code); // 使用自定义的 SmsUtils 发送短信

                // 获取当前时间戳作为发送时间
                int sendTime = (int) (System.currentTimeMillis() / 1000);

                // 记录发送信息
                SmsCrond smsTask = new SmsCrond();
                smsTask.setPhone(phone);
                smsTask.setStatus(1);  // 设置状态为已发送
                smsTask.setContent(code);  // 记录验证码内容
                smsTask.setResult(result ? "短信发送成功" : "短信发送失败");  // 记录发送结果
                smsTask.setSendTime(sendTime);  // 当前时间戳，作为发送时间
                smsTask.setCreatedTime(BaseUtils.currentSeconds());  // 创建时间
                smsTask.setUpdatedTime(BaseUtils.currentSeconds());  // 更新时间
                smsTask.setIsDeleted(0);  // 默认未删除
                mapper.insert(smsTask);
            });
            count++;
        }
        // 提交完所有任务后，关闭线程池
        executorService.shutdown();
        return count;
    }


    // 记录一个新的发送任务（异步发送）
    public int sendAsync(String phone) {
        SmsCrond smsTask = new SmsCrond();
        smsTask.setPhone(phone);
        smsTask.setSendTime(BaseUtils.currentSeconds());  // 当前时间戳，作为发送时间
        smsTask.setStatus(0);  // 设置状态为待发送
        smsTask.setResult("未发送");  // 初始结果为空
        smsTask.setCreatedTime(BaseUtils.currentSeconds());  // 创建时间
        smsTask.setUpdatedTime(BaseUtils.currentSeconds());  // 更新时间
        smsTask.setIsDeleted(0);  // 默认未删除
        // 插入到数据库
        return mapper.insert(smsTask);
    }

    // 同步发送短信并记录结果
    public int sendSmsSync(String phone) {
        // 获取当前时间戳作为发送时间
        int sendTime = (int)(System.currentTimeMillis() / 1000);

//        String code = "验证码：" + (int) (Math.random() * 900000);  // 随机生成验证码
        String code = (int) (Math.random() * 900000) + "";  // 随机生成验证码

        //发送方法
//         Boolean result = SmsUtils.sms(phone,code); // SDK 或自定义的发送方法
         Boolean result = SmsUtil.sendCheckCode(phone, code); // SDK 或自定义的发送方法
        SmsCrond smsTask = new SmsCrond();

        // 记录发送信息
        smsTask.setPhone(phone);
        smsTask.setStatus(1);  // 设置状态为已发送
        smsTask.setContent(code);
        smsTask.setResult(result?"短信发送成功":"短信发送失败");  // 记录发送结果
        smsTask.setSendTime(sendTime);  // 当前时间戳，作为发送时间
        smsTask.setCreatedTime(BaseUtils.currentSeconds());  // 发送时间
        smsTask.setUpdatedTime(BaseUtils.currentSeconds());
        smsTask.setIsDeleted(0);  // 默认未删除
        return mapper.insert(smsTask);  // 记录发送记录
    }

}

