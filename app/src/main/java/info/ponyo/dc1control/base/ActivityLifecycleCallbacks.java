package info.ponyo.dc1control.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import info.ponyo.dc1control.network.socket.ConnectionManager;

/**
 * @author zxq
 * @Date 2019/7/31.
 * @Description:
 */
public class ActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        ConnectionManager.getInstance().start();
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
