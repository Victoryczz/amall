package seu.vczz.amall.pojo;

import lombok.*;

import java.util.Date;

//@Data//set/get、equals()、hashcode()等方法
@Getter
@Setter
@NoArgsConstructor//无参构造器
@AllArgsConstructor//全参构造器
@ToString()//重写toString()方法，默认重写所有属性
public class Cart {
    private Integer id;

    private Integer userId;

    private Integer productId;

    private Integer quantity;

    private Integer checked;

    private Date createTime;

    private Date updateTime;


}