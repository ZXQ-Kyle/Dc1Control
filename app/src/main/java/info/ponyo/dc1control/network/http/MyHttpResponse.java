package info.ponyo.dc1control.network.http;

public class MyHttpResponse<T> {
    public static final int CODE_SUCCESS = 200;
    public static final int CODE_FAILED = 403;

    private int code;
    private String message;
    private T data;

    public int getCode() {
        return code;
    }

    public MyHttpResponse<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public MyHttpResponse<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public MyHttpResponse<T> setData(T data) {
        this.data = data;
        return this;
    }
}
