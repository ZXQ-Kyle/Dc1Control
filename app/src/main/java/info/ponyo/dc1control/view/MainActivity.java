package info.ponyo.dc1control.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

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
    /**
     * 维护fragment栈
     */
    private ArrayList<Fragment> fragmentStack = new ArrayList<>(3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
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
                        .show(deviceFragment)
                        .hide(planFragment)
                        .commit();
                break;
            }
            case "AddPlanFragment": {
                getFragmentTransaction()
                        .show(planFragment)
                        .hide(addPlanFragment)
                        .commit();
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
        if (event.getCode().equals(Event.CODE_JUMP_TO_PLAN)) {
            FragmentTransaction transaction = getFragmentTransaction();
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
        } else if (event.getCode().equals(Event.CODE_JUMP_TO_ADD_PLAN)) {
            FragmentTransaction transaction = getFragmentTransaction();
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
        }
    }

    private FragmentTransaction getFragmentTransaction() {
        return getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
    }
}
