package info.ponyo.dc1control.socket;

import com.google.gson.Gson;

import java.util.ArrayList;

import info.ponyo.dc1control.bean.PlanBean;

/**
 * @author zxq
 * @Date 2019/8/2.
 * @Description:
 */
public class ConnectApi {

    private static Gson gson = new Gson();

    public static void queryDc1List() {
        Connection.getInstance().appendMsgToQueue("queryDevice");
    }

    public static void queryPlanList(String deviceId) {
        Connection.getInstance().appendMsgToQueue("queryPlan " + deviceId);
    }

    public static void switchDc1Status(String id, String status) {
        Connection.getInstance().appendMsgToQueue("set id=" + id + " status=" + status);
    }

    public static void updateDc1Name(String id, ArrayList names) {
        Connection.getInstance().appendMsgToQueue("changeName id=" + id + " names=" + gson.toJson(names));
    }

    public static void resetPower(String id) {
        Connection.getInstance().appendMsgToQueue("resetPower id=" + id);
    }

    public static void addPlan(PlanBean bean) {
        Connection.getInstance().appendMsgToQueue("addPlan " + gson.toJson(bean));
    }

    public static void enablePlanById(String id, boolean enable) {
        Connection.getInstance().appendMsgToQueue("enablePlanById " + id + " " + enable);
    }

    public static void deletePlanById(String id) {
        Connection.getInstance().appendMsgToQueue("deletePlan " + id);
    }
}
