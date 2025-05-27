package red.mlz.app.module.sms_crond.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import red.mlz.common.module.sms_crond.entity.SmsCrond;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface SmsCrondMapper {

// 获取类目列表

@Select("SELECT * FROM sms_crond WHERE status = 0 AND is_deleted=0")
List<SmsCrond> getAll();


// 根据ID查询操作
@Select("SELECT * FROM sms_crond WHERE id =  #{id} AND is_deleted=0")
SmsCrond getById(@Param("id")BigInteger id);

// 根据ID提取操作
@Select("SELECT * FROM sms_crond WHERE id =  #{id}")
SmsCrond extractById(@Param("id")BigInteger id);

// 插入操作
int insert(@Param("smsCrond") SmsCrond smsCrond);

// 乐观锁
@Update("UPDATE sms_crond SET status = 100 WHERE id = #{id} AND status = 0")
int lockTask(@Param("id") BigInteger id);

// 更新操作
int update(@Param("smsCrond") SmsCrond smsCrond);


// 删除操作
@Update("UPDATE sms_crond SET updated_time = #{time} , is_deleted = 1 WHERE id = #{id}")
int delete(@Param("id")BigInteger id,@Param("time") Integer time );


}