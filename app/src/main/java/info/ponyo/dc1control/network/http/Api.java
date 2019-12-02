package info.ponyo.dc1control.network.http;


import java.util.List;

import info.ponyo.dc1control.bean.Dc1Bean;
import info.ponyo.dc1control.bean.PlanBean;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;


/**
 * @author z
 */
public interface Api {

    String BASE_URL = "http://192.168.50.50:8880/";


    /**
     * @param url 通用请求
     * @return
     */
    @GET
    Call<ResponseBody> commonRequest(@Url String url);

    @GET("api/queryDeviceList")
    @FormUrlEncoded
    Call<MyHttpResponse<List<Dc1Bean>>> queryDeviceList(
            @Query("token") String token
    );

    @GET("api/queryPlanList")
    @FormUrlEncoded
    Call<MyHttpResponse<List<PlanBean>>> queryPlanList(
            @Query("token") String token,
            @Query("deviceId") String deviceId
    );
}
