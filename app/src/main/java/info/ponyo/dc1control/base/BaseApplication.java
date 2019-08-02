package info.ponyo.dc1control.base;

import android.app.Application;


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
    }
}
