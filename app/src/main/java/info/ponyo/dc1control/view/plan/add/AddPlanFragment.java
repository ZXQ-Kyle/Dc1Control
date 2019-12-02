package info.ponyo.dc1control.view.plan.add;


import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.annimon.stream.Stream;
import com.contrarywind.view.WheelView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.ponyo.dc1control.R;
import info.ponyo.dc1control.base.CommonAdapter;
import info.ponyo.dc1control.base.CommonViewHolder;
import info.ponyo.dc1control.base.OnRecyclerViewItemClickListener;
import info.ponyo.dc1control.bean.Dc1Bean;
import info.ponyo.dc1control.bean.PlanBean;
import info.ponyo.dc1control.network.socket.ConnectApi;
import info.ponyo.dc1control.util.Event;
import info.ponyo.dc1control.util.SnackUtil;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author zxq
 */
public class AddPlanFragment extends Fragment implements TimePickerDialog.OnTimeSetListener, View.OnClickListener, OnRecyclerViewItemClickListener {
    private Dc1Bean dc1Bean;

    @BindView(R.id.root)
    public ConstraintLayout root;

    @BindView(R.id.tv_trigger_time_label)
    public TextView tvTriggerTimeLabel;
    @BindView(R.id.tv_trigger_time)
    public TextView tvTriggerTime;
    @BindView(R.id.tv_1)
    public TextView tv1;
    @BindView(R.id.tv_2)
    public TextView tv2;
    @BindView(R.id.tv_3)
    public TextView tv3;
    @BindView(R.id.tv_4)
    public TextView tv4;
    @BindView(R.id.sb_1)
    public SwitchCompat sb1;
    @BindView(R.id.sb_2)
    public SwitchCompat sb2;
    @BindView(R.id.sb_3)
    public SwitchCompat sb3;
    @BindView(R.id.sb_4)
    public SwitchCompat sb4;
    @BindView(R.id.fab_add)
    public FloatingActionButton fab;
    @BindView(R.id.rv_weekday)
    public RecyclerView recyclerView;
    @BindView(R.id.cb_repeat_once)
    public CheckBox cbRepeatOnce;
    @BindView(R.id.cb_repeat_everyday)
    public CheckBox cbRepeatEveryday;
    @BindView(R.id.cb_repeat_at_fixed_rate)
    public CheckBox cbRepeatAtFixedRate;
    @BindView(R.id.cb_repeat_customize)
    public CheckBox cbRepeatCustomize;

    @BindView(R.id.tv_hint)
    public TextView tvHint;

    @BindView(R.id.wv_switch)
    public WheelView wvSwitch;
    @BindView(R.id.wv_period)
    public WheelView wvPeriod;
    @BindView(R.id.wv_time)
    public WheelView wvTime;
    @BindView(R.id.cl_repeat_at_fixed_rate)
    public ConstraintLayout clRepeatAtFixedRate;

    private String mTriggerTime;
    private String mRepeat;
    private WeekdayAdapter mAdapter;

    public static AddPlanFragment newInstance() {
        AddPlanFragment fragment = new AddPlanFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_plan, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        tvTriggerTimeLabel.setOnClickListener(this);
        tvTriggerTime.setOnClickListener(this);
        cbRepeatOnce.setOnClickListener(this);
        cbRepeatEveryday.setOnClickListener(this);
        cbRepeatAtFixedRate.setOnClickListener(this);
        cbRepeatCustomize.setOnClickListener(this);
        fab.setOnClickListener(this::onClick);
        cbRepeatEveryday.setChecked(true);

        initNames();

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        mAdapter = new WeekdayAdapter();
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

        List<String> switchIndexList;
        ArrayList<String> names = dc1Bean.getNames();
        if (names != null && names.size() == 5) {
            switchIndexList = new ArrayList<>(4);
            for (int i = 1; i < names.size(); i++) {
                String str = names.get(i);
                if (TextUtils.isEmpty(str)) {
                    if (i == 1) {
                        switchIndexList.add("总开关");
                    } else {
                        switchIndexList.add("开关" + (i - 1));
                    }
                } else {
                    switchIndexList.add(str);
                }
            }
        } else {
            switchIndexList = new ArrayList<>();
            switchIndexList.add("总开关");
            switchIndexList.add("开关1");
            switchIndexList.add("开关2");
            switchIndexList.add("开关3");
        }
        StringWheelAdapter switchAdapter = new StringWheelAdapter(switchIndexList);
        wvSwitch.setCyclic(false);
        wvSwitch.setAdapter(switchAdapter);

        AtomicInteger period = new AtomicInteger();
        AtomicInteger time = new AtomicInteger();
        List<String> periodList = Stream.rangeClosed(1, 60).map(Object::toString).toList();
        StringWheelAdapter periodAdapter = new StringWheelAdapter(periodList);
        wvPeriod.setAdapter(periodAdapter);
        wvPeriod.setCyclic(false);
        wvPeriod.setOnItemSelectedListener(index -> {
            period.set(index + 1);
            tvHint.setText(String.format("每%d分钟执行一次，每次开启%d分钟", period.get(), time.get()));
        });

        List<String> timeList = Stream.rangeClosed(1, 60).map(Object::toString).toList();
        StringWheelAdapter timeAdapter = new StringWheelAdapter(timeList);
        wvTime.setAdapter(timeAdapter);
        wvPeriod.setCyclic(false);
        wvTime.setOnItemSelectedListener(index -> {
            time.set(index + 1);
            tvHint.setText(String.format("每%d分钟执行一次，每次开启%d分钟", period.get(), time.get()));
        });
    }

    private void initNames() {
        if (dc1Bean != null) {
            //开关名称及状态
            ArrayList<String> names = dc1Bean.getNames();
            if (names != null && names.size() == 5) {
                tv1.setText(TextUtils.isEmpty(names.get(1)) ? "1. 总开关" : "1. " + names.get(1));
                tv2.setText(TextUtils.isEmpty(names.get(2)) ? "2. 开关" : "2. " + names.get(2));
                tv3.setText(TextUtils.isEmpty(names.get(3)) ? "3. 开关" : "3. " + names.get(3));
                tv4.setText(TextUtils.isEmpty(names.get(4)) ? "4. 开关" : "4. " + names.get(4));
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        initNames();
    }

    public AddPlanFragment setDc1Bean(Dc1Bean dc1Bean) {
        this.dc1Bean = dc1Bean;
        return this;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        StringBuilder sb = new StringBuilder();
        if (hourOfDay < 10) {
            sb.append("0");
        }
        sb.append(hourOfDay).append(":");
        if (minute < 10) {
            sb.append("0");
        }
        sb.append(minute).append(":").append("00");
        mTriggerTime = sb.toString();
        tvTriggerTime.setText(mTriggerTime);
    }

    @Override
    public void onClick(View v) {
        TransitionManager.beginDelayedTransition(root);
        switch (v.getId()) {
            case R.id.tv_trigger_time_label:
            case R.id.tv_trigger_time: {
                new TimePickerDialog(getContext(), this, 0, 0, true)
                        .show();
                break;
            }
            case R.id.fab_add: {
                save();
                break;
            }
            case R.id.cb_repeat_once: {
                cbRepeatOnce.setChecked(true);
                cbRepeatEveryday.setChecked(false);
                cbRepeatAtFixedRate.setChecked(false);
                cbRepeatCustomize.setChecked(false);
                mAdapter.clearState();
                recyclerView.setVisibility(View.GONE);
                clRepeatAtFixedRate.setVisibility(View.GONE);
                break;
            }
            case R.id.cb_repeat_everyday: {
                cbRepeatOnce.setChecked(false);
                cbRepeatEveryday.setChecked(true);
                cbRepeatAtFixedRate.setChecked(false);
                cbRepeatCustomize.setChecked(false);
                mAdapter.clearState();
                recyclerView.setVisibility(View.GONE);
                clRepeatAtFixedRate.setVisibility(View.GONE);
                break;
            }
            case R.id.cb_repeat_at_fixed_rate: {
                cbRepeatOnce.setChecked(false);
                cbRepeatEveryday.setChecked(false);
                cbRepeatAtFixedRate.setChecked(true);
                cbRepeatCustomize.setChecked(false);
                mAdapter.clearState();
                recyclerView.setVisibility(View.GONE);
                clRepeatAtFixedRate.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.cb_repeat_customize: {
                cbRepeatOnce.setChecked(false);
                cbRepeatEveryday.setChecked(false);
                cbRepeatAtFixedRate.setChecked(false);
                cbRepeatCustomize.setChecked(true);
                recyclerView.setVisibility(View.VISIBLE);
                clRepeatAtFixedRate.setVisibility(View.GONE);
                break;
            }
            default: {
                break;
            }
        }
    }

    private void save() {
        if (TextUtils.isEmpty(mTriggerTime)) {
            SnackUtil.snack(tvTriggerTime, "必须设置触发时间");
            return;
        }

        boolean success = calcRepeat();
        if (!success) {
            Toast.makeText(getContext(), "周期设置有误", Toast.LENGTH_SHORT).show();
            return;
        }

        String sb1Command = this.sb1.isChecked() ? "1" : "0";
        String sb2Command = this.sb2.isChecked() ? "1" : "0";
        String sb3Command = this.sb3.isChecked() ? "1" : "0";
        String sb4Command = this.sb4.isChecked() ? "1" : "0";

        PlanBean planBean = new PlanBean()
                .setId(UUID.randomUUID().toString())
                .setEnable(true)
                .setTriggerTime(mTriggerTime)
                .setStatus(sb1Command + sb2Command + sb3Command + sb4Command)
                .setDeviceName(dc1Bean.getNames() == null || dc1Bean.getNames().isEmpty() ? "" : dc1Bean.getNames().get(0))
                .setRepeat(mRepeat)
                .setDeviceId(dc1Bean.getId())
                .setCommand("1")
                .setSwitchIndex(wvSwitch.getCurrentItem() + "")
                .setRepeatData(String.format("%d,%d", wvPeriod.getCurrentItem() + 1, wvTime.getCurrentItem() + 1));
        ConnectApi.addPlan(planBean);
        EventBus.getDefault().post(new Event().setCode(Event.CODE_ADD_PLAN).setData(planBean));

        mTriggerTime = null;
        tvTriggerTime.setText("");
        mRepeat = null;
        cbRepeatEveryday.performClick();
        sb1.setChecked(true);
        sb2.setChecked(true);
        sb3.setChecked(true);
        sb4.setChecked(true);

        getActivity().onBackPressed();
    }

    private boolean calcRepeat() {
        if (cbRepeatAtFixedRate.isChecked()) {
            mRepeat = PlanBean.REPEAT_AT_FIXED_RATE;
            return true;
        }
        if (cbRepeatOnce.isChecked()) {
            mRepeat = PlanBean.REPEAT_ONCE;
            return true;
        }
        if (cbRepeatEveryday.isChecked()) {
            mRepeat = PlanBean.REPEAT_EVERYDAY;
            return true;
        }
        if (cbRepeatCustomize.isChecked()) {
            boolean[] selectState = mAdapter.getSelectState();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < selectState.length; i++) {
                if (selectState[i]) {
                    sb.append(i + 1).append(",");
                }
            }
            if (sb.length() == 0) {
                return false;
            }
            if (sb.length() == 14) {
                mRepeat = PlanBean.REPEAT_EVERYDAY;
                return true;
            }
            mRepeat = sb.substring(0, sb.length() - 1);
            return true;
        }
        return false;
    }

    @Override
    public void onItemClick(CommonAdapter adapter, CommonViewHolder holder, int position) {
        if (!cbRepeatCustomize.isChecked()) {
            cbRepeatCustomize.performClick();
        }
        boolean[] selectState = mAdapter.getSelectState();
        selectState[position] = !selectState[position];
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void onItemChildClick(CommonAdapter adapter, CommonViewHolder holder, int viewId, int position) {

    }
}
