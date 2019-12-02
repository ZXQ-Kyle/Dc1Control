package info.ponyo.dc1control.network.http;

import androidx.annotation.Nullable;

/**
 * @author zxq
 * @Date 2019/11/18.
 * @Description:
 */
public interface IHttpCallback<T> {

    /**
     * 请求成功
     *
     * @param data 可能为空
     */
    void onSuccess(@Nullable T data);

    /**
     * 请求失败
     *
     * @param message
     */
    void onFailure(String message);
}
