package info.ponyo.dc1control.base;

import android.app.Application;

import info.ponyo.dc1control.util.SpManager;


/**
 * @author zxq
 * @Date 2019/7/31.
 * @Description:
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new info.ponyo.dc1control.base.ActivityLifecycleCallbacks());
        SpManager.initInstance(this);
    }
}
