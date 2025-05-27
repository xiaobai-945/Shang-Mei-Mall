package red.mlz.common.module.goods.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigInteger;


@Data
public class Category {

    // 类目id
    private BigInteger id;
    // 父类目id
    @ExcelProperty("parentId")
    private BigInteger parentId;
    //类目名称
    @ExcelProperty("name")
    private String name;
    //类目图片
    @ExcelProperty("image")
    private String image;
    //创建时间
    private Integer createdTime;
    //更新时间
    private Integer updatedTime;
    //删除
    private Integer isDeleted;

}