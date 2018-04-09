package seu.vczz.amall.common;

/**
 * CREATE by vczz on 2018/4/9
 * 响应状态码,0表示成功、1表示失败、10表示需要登录、2表示参数错误
 */
public enum ResponseCode {

    //枚举
    SUCCESS(0, "SUCCESS"),
    ERROE(1, "ERROR"),
    NEED_LOGIN(10, "NEED_LOGIN"),
    ILLEGAL_ARGUMENT(2, "ILLEGAL_ARGUMENT");

    private final int code;
    private final String desc;
    //构造
    ResponseCode(int code, String desc){
        this.code = code;
        this.desc = desc;
    }
    //getCode
    public int getCode(){
        return code;
    }
    //getDesc
    public String getDesc(){
        return desc;
    }

}
