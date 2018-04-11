package seu.vczz.amall.pojo;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")//重写了equls和hashcode()方法，只作用在id上
@ToString(exclude = "updateTime")
public class Category {
    private Integer id;

    private Integer parentId;

    private String name;

    private Boolean status;

    private Integer sortOrder;

    private Date createTime;

    private Date updateTime;



    //重写hashcode和equals，因为要判断category是否相同
    //只需要比较id即可


}