package info.ponyo.dc1control.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import info.ponyo.dc1control.R;
import info.ponyo.dc1control.bean.Dc1Bean;
import info.ponyo.dc1control.util.Event;
import info.ponyo.dc1control.view.device.DeviceFragment;
import info.ponyo.dc1control.view.plan.PlanFragment;
import info.ponyo.dc1control.view.plan.add.AddPlanFragment;

/**
 * @author zxq
 */
public class MainActivity extends AppCompatActivity {

    private DeviceFragment deviceFragment;
    private PlanFragment planFragment;
    private AddPlanFragment addPlanFragment;
    private ArrayList<Fragment> fragmentList = new ArrayList<>(3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        if (deviceFragment == null) {
            deviceFragment = DeviceFragment.newInstance();
            fragmentList.add(deviceFragment);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, deviceFragment, DeviceFragment.class.getSimpleName())
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .show(deviceFragment)
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (deviceFragment.isHidden()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .show(deviceFragment)
                    .hide(planFragment)
                    .commit();
        } else {
            super.onBackPressed();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event event) {
        if (event.getCode().equals(Event.CODE_JUMP_TO_PLAN)) {
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            transaction.hide(deviceFragment);
            if (planFragment == null) {
                planFragment = PlanFragment.newInstance();
                transaction.add(R.id.container, planFragment);
                fragmentList.add(planFragment);
            } else {
                transaction.show(planFragment);
            }
            transaction.commit();
            planFragment.setDc1Bean((Dc1Bean) event.getData());
        } else if (event.getCode().equals(Event.CODE_JUMP_TO_ADD_PLAN)) {
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            transaction.hide(planFragment);
            if (addPlanFragment == null) {
                addPlanFragment = AddPlanFragment.newInstance();
                transaction.add(R.id.container, addPlanFragment);
                fragmentList.add(addPlanFragment);
            } else {
                transaction.show(addPlanFragment);
            }
            transaction.commit();
            addPlanFragment.setDc1Bean((Dc1Bean) event.getData());
        }
    }
}
