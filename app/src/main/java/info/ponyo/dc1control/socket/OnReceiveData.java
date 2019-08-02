package info.ponyo.dc1control.socket;

import java.util.ArrayList;

import info.ponyo.dc1control.bean.Dc1Bean;

/**
 * @author zxq
 * @Date 2019/7/31.
 * @Description:
 */
public interface OnReceiveData {
    void onReceive(ArrayList<Dc1Bean> list);
}
