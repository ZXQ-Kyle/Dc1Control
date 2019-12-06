package info.ponyo.dc1control.network.http;


import java.util.List;

import info.ponyo.dc1control.bean.Dc1Bean;
import info.ponyo.dc1control.bean.PlanBean;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;


/**
 * @author z
 */
public interface Api {

    String BASE_URL = "http://frp.ponyo.space:50008/";


    /**
     * @param url 通用请求
     * @return
     */
    @GET
    Call<ResponseBody> commonRequest(@Url String url);

    @GET("api/queryDeviceList")
    Call<MyHttpResponse<List<Dc1Bean>>> queryDeviceList(
            @Query("token") String token
    );

    @GET("api/queryPlanList")
    Call<MyHttpResponse<List<PlanBean>>> queryPlanList(
            @Query("token") String token,
            @Query("deviceId") String deviceId
    );

    @POST("api/addPlan")
    Call<MyHttpResponse<String>> addPlan(
            @Query("token") String token,
            @Body PlanBean planBean
    );
}
