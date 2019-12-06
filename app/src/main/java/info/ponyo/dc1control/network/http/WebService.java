package info.ponyo.dc1control.network.http;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

import info.ponyo.dc1control.util.Const;
import info.ponyo.dc1control.util.SpManager;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebService {

    private static String BASE_URL = "";

    private static Api api;

    public static Api get() {
        return api;
    }

    static {
        createApi();
    }

    public static void createApi() {
        String host = SpManager.getString(Const.KEY_HOST, "192.168.1.1");
        String httpPort = SpManager.getString(Const.KEY_HTTP_PORT, "8880");
        if (!host.startsWith("http")) {
            host = "http://" + host;
        }
        BASE_URL = host + ":" + httpPort;
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new NetworkInterceptor())
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                //设置数据解析器
                .addConverterFactory(GsonConverterFactory.create())
                //设置网络请求的Url地址
                .baseUrl(BASE_URL)
                .build();
        api = retrofit.create(Api.class);
    }

    /**
     * <font size="33" >异步</font>
     *
     * @param call
     * @param iHttpCallback
     * @param <T>
     */
    public static <T> void enqueue(Call<MyHttpResponse<T>> call, IHttpCallback<T> iHttpCallback) {
        call.enqueue(new Callback<MyHttpResponse<T>>() {
            @Override
            public void onResponse(Call<MyHttpResponse<T>> call, Response<MyHttpResponse<T>> response) {
                if (iHttpCallback == null) {
                    return;
                }
                MyHttpResponse<T> body = response.body();
                if (!response.isSuccessful()) {
                    iHttpCallback.onFailure("网络请求失败");
                    return;
                }
                if (body == null) {
                    iHttpCallback.onFailure("网络数据异常");
                    return;
                }
                if (body.getCode() != MyHttpResponse.CODE_SUCCESS) {
                    iHttpCallback.onFailure(body.getMessage());
                    return;
                }
                iHttpCallback.onSuccess(body.getData());
            }

            @Override
            public void onFailure(Call<MyHttpResponse<T>> call, Throwable t) {
                t.printStackTrace();
                if (iHttpCallback != null) {
                    iHttpCallback.onFailure(t.getMessage());
                }
            }
        });
    }

    /**
     * 同步
     *
     * @param call
     * @param iHttpCallback
     * @param <T>
     */
    public static <T> void execute(Call<MyHttpResponse<T>> call, IHttpCallback<T> iHttpCallback) {
        Response<MyHttpResponse<T>> execute;
        try {
            execute = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
            if (iHttpCallback != null) {
                iHttpCallback.onFailure("io异常");
            }
            return;
        }
        if (iHttpCallback == null) {
            return;
        }
        MyHttpResponse<T> body = execute.body();
        if (!execute.isSuccessful()) {
            iHttpCallback.onFailure("网络请求失败");
            return;
        }
        if (body == null) {
            iHttpCallback.onFailure("网络数据异常");
            return;
        }
        if (body.getCode() != MyHttpResponse.CODE_SUCCESS) {
            iHttpCallback.onFailure(body.getMessage());
            return;
        }
        iHttpCallback.onSuccess(body.getData());
    }
}
