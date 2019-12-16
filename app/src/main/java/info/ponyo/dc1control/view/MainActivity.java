package info.ponyo.dc1control.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import info.ponyo.dc1control.BuildConfig;
import info.ponyo.dc1control.R;
import info.ponyo.dc1control.bean.Dc1Bean;
import info.ponyo.dc1control.util.Event;
import info.ponyo.dc1control.view.device.DeviceFragment;
import info.ponyo.dc1control.view.plan.PlanFragment;
import info.ponyo.dc1control.view.plan.add.AddPlanFragment;
import info.ponyo.dc1control.view.plan.countdown.CountDownFragment;

/**
 * @author zxq
 */
public class MainActivity extends AppCompatActivity {

    private DeviceFragment deviceFragment;
    private PlanFragment planFragment;
    private AddPlanFragment addPlanFragment;

    /**
     * 维护fragment栈
     */
    private ArrayList<Fragment> fragmentStack = new ArrayList<>(3);
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setSubtitle("设备列表");
        getSupportActionBar().setTitle(getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME);

        if (savedInstanceState != null) {
            deviceFragment = (DeviceFragment) getSupportFragmentManager().getFragment(savedInstanceState, DeviceFragment.class.getSimpleName());
            planFragment = (PlanFragment) getSupportFragmentManager().getFragment(savedInstanceState, PlanFragment.class.getSimpleName());
            addPlanFragment = (AddPlanFragment) getSupportFragmentManager().getFragment(savedInstanceState, AddPlanFragment.class.getSimpleName());
        }


        if (deviceFragment == null) {
            deviceFragment = DeviceFragment.newInstance();
            getFragmentTransaction()
                    .add(R.id.container, deviceFragment, DeviceFragment.class.getSimpleName())
                    .commit();
        } else {
            getFragmentTransaction()
                    .show(deviceFragment)
                    .commit();
        }
        fragmentStack.clear();
        fragmentStack.add(deviceFragment);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (deviceFragment != null) {
            getSupportFragmentManager().putFragment(outState, DeviceFragment.class.getSimpleName(), deviceFragment);
        }
        if (planFragment != null) {
            getSupportFragmentManager().putFragment(outState, PlanFragment.class.getSimpleName(), planFragment);
        }
        if (addPlanFragment != null) {
            getSupportFragmentManager().putFragment(outState, AddPlanFragment.class.getSimpleName(), addPlanFragment);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (fragmentStack.isEmpty()) {
            super.onBackPressed();
            return;
        }
        int lastIndex = fragmentStack.size() - 1;
        Fragment fragment = fragmentStack.get(lastIndex);
        fragmentStack.remove(lastIndex);
        switch (fragment.getClass().getSimpleName()) {
            case "PlanFragment": {
                getFragmentTransaction()
                        .setCustomAnimations(R.anim.fragment_left_enter, R.anim.fragment_right_exit)
                        .show(deviceFragment)
                        .hide(planFragment)
                        .commit();
                getSupportActionBar().setSubtitle("设备列表");
                break;
            }
            case "AddPlanFragment": {
                getFragmentTransaction()
                        .setCustomAnimations(R.anim.fragment_left_enter, R.anim.fragment_right_exit)
                        .show(planFragment)
                        .hide(addPlanFragment)
                        .commit();
                getSupportActionBar().setSubtitle("计划列表");
                break;
            }
            case "CountDownFragment": {
                getFragmentTransaction()
                        .setCustomAnimations(R.anim.fragment_left_enter, R.anim.fragment_right_exit)
                        .show(deviceFragment)
                        .remove(fragment)
                        .commit();
                getSupportActionBar().setSubtitle("设备列表");
                break;
            }
            default: {
                super.onBackPressed();
                break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event event) {
        switch (event.getCode()) {
            case Event.CODE_JUMP_TO_PLAN: {
                FragmentTransaction transaction = getFragmentTransaction();
                transaction.setCustomAnimations(R.anim.fragment_right_enter, R.anim.fragment_left_exit, R.anim.fragment_left_enter, R.anim.fragment_left_exit);
                transaction.hide(deviceFragment);
                if (planFragment == null) {
                    planFragment = PlanFragment.newInstance();
                    transaction.add(R.id.container, planFragment, PlanFragment.class.getSimpleName());
                } else {
                    transaction.show(planFragment);
                }
                transaction.commit();
                fragmentStack.add(planFragment);
                planFragment.setDc1Bean((Dc1Bean) event.getData());
                getSupportActionBar().setSubtitle("计划列表");
                break;
            }
            case Event.CODE_DEVICE_JUMP_TO_COUNT_DOWN: {
                CountDownFragment countDownFragment = CountDownFragment.newInstance((Dc1Bean) event.getData());
                FragmentTransaction transaction = getFragmentTransaction();
                transaction.setCustomAnimations(R.anim.fragment_right_enter, R.anim.fragment_left_exit, R.anim.fragment_left_enter, R.anim.fragment_left_exit).hide(deviceFragment)
                        .add(R.id.container, countDownFragment, CountDownFragment.class.getSimpleName())
                        .commit();
                fragmentStack.add(countDownFragment);
                getSupportActionBar().setSubtitle("关闭倒计时");
                break;
            }
            case Event.CODE_JUMP_TO_ADD_PLAN: {
                FragmentTransaction transaction = getFragmentTransaction();
                transaction.setCustomAnimations(R.anim.fragment_right_enter, R.anim.fragment_left_exit, R.anim.fragment_left_enter, R.anim.fragment_left_exit);
                transaction.hide(planFragment);
                if (addPlanFragment == null) {
                    addPlanFragment = AddPlanFragment.newInstance();
                    transaction.add(R.id.container, addPlanFragment, AddPlanFragment.class.getSimpleName());
                } else {
                    transaction.show(addPlanFragment);
                }
                transaction.commit();
                fragmentStack.add(addPlanFragment);
                addPlanFragment.setDc1Bean((Dc1Bean) event.getData());
                getSupportActionBar().setSubtitle("添加计划");
                break;
            }
            case Event.CODE_CONNECT_ERROR: {
                break;
            }
            case Event.CODE_MESSAGE: {
                break;
            }
            default: {
                break;
            }
        }
    }

    private void showServerUnconnectDialog() {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(this)
                    .setTitle("警告")
                    .setMessage("服务器连接失败")
                    .setPositiveButton("确定", (dialog, which) -> dialog.dismiss())
                    .setPositiveButtonIcon(getResources().getDrawable(R.drawable.ic_confirm))
                    .create();
        }
        if (mAlertDialog.isShowing()) {
            return;
        }
        mAlertDialog.show();
    }

    private FragmentTransaction getFragmentTransaction() {
        return getSupportFragmentManager()
                .beginTransaction();
    }
}
