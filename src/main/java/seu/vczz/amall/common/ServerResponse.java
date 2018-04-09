package seu.vczz.amall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * CREATE by vczz on 2018/4/9
 * 前后端交互消息类
 */
//为空的字段不需要返回
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> implements Serializable{

    //状态量
    private int status;
    //返回信息
    private String msg;
    //返回的泛型数据
    private T data;

    //私有化构造方法
    private ServerResponse(int status){
        this.status = status;
    }
    private ServerResponse(int status, String msg){
        this.status = status;
        this.msg = msg;
    }
    //当调用string时，其实使用的是上边的函数，其他类型就是下边的构造函数，但是
    //当要在data里边放的是string时，就会出现问题，之后代码会规避这个问题
    private ServerResponse(int status, T data){
        this.status = status;
        this.data = data;
    }
    private ServerResponse(int status, String msg, T data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 判断是否请求成功
     * 注解解决序列化时候该方法也会出现在json
     */
    @JsonIgnore
    public boolean isSuccess(){
        return this.status == ResponseCode.SUCCESS.getCode();
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }
    /**
     * 静态泛型方法，静态方法不能访问类定义的泛型，所以讲泛型定义在方法上
     * 返回成功状态
     */
    public static <T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }
    //这样就就将string类型的data和msg区别开来

    /**
     * 返回成功状态码的同时，返回状态信息
     */
    public static <T> ServerResponse<T> createBySuccessMessage(String msg){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), msg);
    }

    /**
     * 返回成功状态码的同时，返回一个自定义类型数据
     */
    public static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), data);
    }

    /**
     * 同时返回成功状态码、状态信息、数据
     */
    public static <T> ServerResponse<T> createBySuccess(String msg, T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), msg, data);
    }
    /**
     * 放回错误状态
     */
    public static <T> ServerResponse<T> createByError(){
        return new ServerResponse<T>(ResponseCode.ERROE.getCode());
    }

    /**
     * 返回错误状态码、错误信息
     */
    public static <T> ServerResponse<T> createByErrorMessage(String errorMsg){
        return new ServerResponse<T>(ResponseCode.ERROE.getCode(), errorMsg);
    }
    /**
     * 返回动态的状态码以及状态信息
     */
    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode, String msg){
        return new ServerResponse<T>(errorCode, msg);
    }


}
