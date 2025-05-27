
package red.mlz.app.module.sms_crond.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import red.mlz.common.module.sms_crond.entity.SmsUse;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface SmsUseMapper {

// 获取类目列表
@Select("SELECT * FROM sms_use WHERE is_deleted=0")
List<SmsUse> getAll();


// 根据ID查询操作
@Select("SELECT * FROM sms_use WHERE id =  #{id} AND is_deleted=0")
SmsUse getById(@Param("id")BigInteger id);

// 根据ID提取操作
@Select("SELECT * FROM sms_use WHERE id =  #{id}")
SmsUse extractById(@Param("id")BigInteger id);

// 插入操作
int insert(@Param("smsUse") SmsUse smsUse);

// 更新操作
int update(@Param("smsUse") SmsUse smsUse);

// 删除操作
@Update("UPDATE sms_use SET updated_time = #{time} , is_deleted = 1 WHERE id = #{id}")
int delete(@Param("id")BigInteger id,@Param("time") Integer time );


}