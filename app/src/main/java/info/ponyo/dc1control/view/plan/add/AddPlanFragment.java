package info.ponyo.dc1control.view.plan.add;


import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.ponyo.dc1control.R;
import info.ponyo.dc1control.bean.Dc1Bean;
import info.ponyo.dc1control.bean.PlanBean;
import info.ponyo.dc1control.socket.ConnectApi;
import info.ponyo.dc1control.util.Event;
import info.ponyo.dc1control.util.SnackUtil;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author zxq
 */
public class AddPlanFragment extends Fragment implements TimePickerDialog.OnTimeSetListener, View.OnClickListener {
    private Dc1Bean dc1Bean;

    @BindView(R.id.tv_trigger_time_label)
    public TextView tvTriggerTimeLabel;
    @BindView(R.id.tv_trigger_time)
    public TextView tvTriggerTime;
    @BindView(R.id.tv_repeat)
    public TextView tvRepeat;
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

    private String mTriggerTime;
    private String mRepeat;

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
        tvRepeat.setOnClickListener(this);
        fab.setOnClickListener(this::onClick);

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

    private void showTimeDialog() {
        new TimePickerDialog(getContext(), this, 0, 0, true)
                .show();
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
        switch (v.getId()) {
            case R.id.tv_trigger_time_label:
            case R.id.tv_trigger_time: {
                showTimeDialog();
                break;
            }
            case R.id.tv_repeat: {
                showRepeatDialog();
                break;
            }
            case R.id.fab_add: {
                save();
                break;
            }
            default: {
                break;
            }
        }
    }

    private void showRepeatDialog() {
        String[] repeat = {"一次", "每天"};
        new AlertDialog.Builder(getContext())
                .setTitle("频率")
                .setIcon(R.drawable.ic_repeat)
                .setItems(repeat, (dialog, which) -> {
                    String s = repeat[which];
                    tvRepeat.setText(s);
                    mRepeat = which == 0 ? PlanBean.REPEAT_ONCE : PlanBean.REPEAT_EVERYDAY;
                })
                .create()
                .show();
    }

    private void save() {
        if (TextUtils.isEmpty(mTriggerTime)) {
            SnackUtil.snack(tvTriggerTime, "必须设置触发时间");
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
                .setDeviceId(dc1Bean.getId());
        ConnectApi.addPlan(planBean);
        EventBus.getDefault().post(new Event().setCode(Event.CODE_ADD_PLAN).setData(planBean));

        mTriggerTime = null;
        tvTriggerTime.setText("");
        mRepeat = null;
        tvRepeat.setText("");
        sb1.setChecked(true);
        sb2.setChecked(true);
        sb3.setChecked(true);
        sb4.setChecked(true);

        getActivity().onBackPressed();
    }
}
