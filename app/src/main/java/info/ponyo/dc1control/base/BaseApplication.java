package info.ponyo.dc1control.base;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.avos.avoscloud.AVOSCloud;
import com.tencent.bugly.Bugly;

import info.ponyo.dc1control.util.SpManager;


/**
 * @author zxq
 * @Date 2019/7/31.
 * @Description:
 */
public class BaseApplication extends Application {

    public static BaseApplication instance;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        registerActivityLifecycleCallbacks(new info.ponyo.dc1control.base.ActivityLifecycleCallbacks());
        SpManager.initInstance(this);
        Bugly.init(this, "2c489a8155", false);
        // 初始化参数依次为 this，App Id，App Key
        AVOSCloud.initialize(this, "6ru2eSdDfKf1oglEUM4OhkmS-9Nh9j0Va", "Wfatn9udffz5Ou4NdbimLgW9");
    }
}
