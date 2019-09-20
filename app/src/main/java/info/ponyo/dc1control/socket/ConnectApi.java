package info.ponyo.dc1control.socket;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.ArrayList;

import info.ponyo.dc1control.bean.PlanBean;
import info.ponyo.dc1control.util.Const;
import info.ponyo.dc1control.util.SpManager;

/**
 * @author zxq
 * @Date 2019/8/2.
 * @Description:
 */
public class ConnectApi {

    private static Gson gson = new Gson();
    public static String token;

    private static String getToken() {
        if (TextUtils.isEmpty(token)) {
            token = SpManager.getString(Const.KEY_TOKEN, "");
        }
        return token;
    }


    public static void queryDc1List() {
        Connection.getInstance().appendMsgToQueue("queryDevice " + getToken());
    }

    public static void queryPlanList(String deviceId) {
        Connection.getInstance().appendMsgToQueue("queryPlan " + getToken() + " " + deviceId);
    }

    public static void switchDc1Status(String id, String status) {
        Connection.getInstance().appendMsgToQueue("set " + getToken() + " id=" + id + " status=" + status);
    }

    public static void updateDc1Name(String id, ArrayList names) {
        Connection.getInstance().appendMsgToQueue("changeName " + getToken() + " id=" + id + " names=" + gson.toJson(names));
    }

    public static void resetPower(String id) {
        Connection.getInstance().appendMsgToQueue("resetPower " + getToken() + " id=" + id);
    }

    public static void addPlan(PlanBean bean) {
        Connection.getInstance().appendMsgToQueue("addPlan " + getToken() + " " + gson.toJson(bean));
    }

    public static void enablePlanById(String id, boolean enable) {
        Connection.getInstance().appendMsgToQueue("enablePlanById " + getToken() + " " + id + " " + enable);
    }

    public static void deletePlanById(String id) {
        Connection.getInstance().appendMsgToQueue("deletePlan " + getToken() + " " + id);
    }
}
