package info.ponyo.dc1control.util;

/**
 * @author zxq
 * @Date 2019/8/26.
 * @Description:
 */
public class Event {
    public static final String CODE_JUMP_TO_PLAN="code_jump_to_plan";
    public static final String CODE_DEVICE_LIST="CODE_DC1_LIST";
    public static final String CODE_PLAN_LIST="CODE_PLAN_LIST";
    public static final String CODE_JUMP_TO_ADD_PLAN = "CODE_JUMP_TO_ADD_PLAN";

    private String code;
    private Object data;

    public String getCode() {
        return code;
    }

    public Event setCode(String code) {
        this.code = code;
        return this;
    }

    public Object getData() {
        return data;
    }

    public Event setData(Object data) {
        this.data = data;
        return this;
    }
}
