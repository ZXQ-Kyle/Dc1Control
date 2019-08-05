package info.ponyo.dc1control.socket;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * @author zxq
 * @Date 2019/8/2.
 * @Description:
 */
public class ConnectApi {

    private static Gson gson = new Gson();

    public static void queryDc1List() {
        Connection.getInstance().appendMsgToQueue("query");
    }

    public static void switchDc1Status(String id, String status) {
        Connection.getInstance().appendMsgToQueue("set id=" + id + " status=" + status);
    }

    public static void updateDc1Name(String id, ArrayList names) {
        Connection.getInstance().appendMsgToQueue("changeName id=" + id + " names=" + gson.toJson(names));
    }
}
