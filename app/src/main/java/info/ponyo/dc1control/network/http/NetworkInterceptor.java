package info.ponyo.dc1control.network.http;

import android.util.Log;

import java.io.IOException;
import java.net.URLDecoder;

import info.ponyo.dc1control.BuildConfig;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * author: zxq
 */

public class NetworkInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (BuildConfig.DEBUG) {
            String responseStr;
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                if (body != null) {
                    responseStr = body.string();
                    ResponseBody newBody = ResponseBody.create(body.contentType(), responseStr);
                    response = response.newBuilder()
                            .body(newBody)
                            .build();
                } else {
                    responseStr = "网络请求成功，但无数据返回";
                }
            } else {
                responseStr = "网络请求失败，请检查网络";
            }
            Log.i("[HTTP]:", "url:" + URLDecoder.decode(request.url().toString(), "UTF-8") + " response:" + responseStr);
        }
        return response;
    }
}
