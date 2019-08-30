package info.ponyo.dc1control.view.plan.add;


import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import info.ponyo.dc1control.R;
import info.ponyo.dc1control.bean.Dc1Bean;
import info.ponyo.dc1control.util.Event;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author zxq
 */
public class AddPlanFragment extends Fragment implements TimePickerDialog.OnTimeSetListener {
    private Dc1Bean dc1Bean;

    public static AddPlanFragment newInstance() {
        AddPlanFragment fragment = new AddPlanFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_plan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findviewbyid(R.id.)
    }

    @Override
    public void onStart() {
        super.onStart();
        initView();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private void initView() {

    }

    private void showTimeDialog() {
        new TimePickerDialog(getContext(), this, 0, 0, true)
                .show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event event) {

    }

    public AddPlanFragment setDc1Bean(Dc1Bean dc1Bean) {
        this.dc1Bean = dc1Bean;
        return this;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }
}
