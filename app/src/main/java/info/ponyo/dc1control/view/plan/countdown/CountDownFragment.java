package info.ponyo.dc1control.view.plan.countdown;


import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.annimon.stream.Stream;
import com.contrarywind.view.WheelView;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.ponyo.dc1control.R;
import info.ponyo.dc1control.base.CommonAdapter;
import info.ponyo.dc1control.base.CommonViewHolder;
import info.ponyo.dc1control.base.OnRecyclerViewItemClickListener;
import info.ponyo.dc1control.bean.Dc1Bean;
import info.ponyo.dc1control.bean.PlanBean;
import info.ponyo.dc1control.network.http.IHttpCallback;
import info.ponyo.dc1control.network.http.WebService;
import info.ponyo.dc1control.network.socket.ConnectApi;
import info.ponyo.dc1control.util.Const;
import info.ponyo.dc1control.util.Event;
import info.ponyo.dc1control.util.SnackUtil;
import info.ponyo.dc1control.util.SpManager;
import info.ponyo.dc1control.view.plan.add.StringWheelAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class CountDownFragment extends Fragment implements View.OnClickListener {

    private static final String KEY = "Dc1Bean";
    private Dc1Bean dc1Bean;

    @BindView(R.id.wv_switch)
    public WheelView wvSwitch;
    @BindView(R.id.wv_hour)
    public WheelView wvHour;
    @BindView(R.id.wv_minute)
    public WheelView wvMinute;
    @BindView(R.id.btn_confirm)
    public Button btnConfirm;
    @BindView(R.id.rv)
    public RecyclerView mRv;

    public static CountDownFragment newInstance(Dc1Bean dc1Bean) {
        CountDownFragment fragment = new CountDownFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY, dc1Bean);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_count_down, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnConfirm.setOnClickListener(this);
        Bundle arguments = getArguments();
        if (arguments != null) {
            dc1Bean = arguments.getParcelable(KEY);
        }
        if (dc1Bean == null) {
            Toast.makeText(this.getContext(), "数据异常", Toast.LENGTH_SHORT).show();
            return;
        }
        initNames();

        List<String> hours = Stream.rangeClosed(0, 23).map(Object::toString).toList();
        StringWheelAdapter hourAdapter = new StringWheelAdapter(hours);
        wvHour.setCyclic(false);
        wvHour.setCurrentItem(0);
        wvHour.setTextColorCenter(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        wvHour.setLabel("时");
        wvHour.setAdapter(hourAdapter);

        List<String> minutes = Stream.rangeClosed(0, 59).map(Object::toString).toList();
        StringWheelAdapter minuteAdapter = new StringWheelAdapter(minutes);
        wvMinute.setCyclic(false);
        wvMinute.setCurrentItem(0);
        wvMinute.setTextColorCenter(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        wvMinute.setLabel("分");
        wvMinute.setAdapter(minuteAdapter);

        mRv.setLayoutManager(new LinearLayoutManager(getContext()));
        RvAdapter adapter = new RvAdapter();
        ArrayList<String> list = new ArrayList<>();
        list.add("10分钟");
        list.add("30分钟");
        list.add("1小时");
        adapter.setData(list);
        mRv.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(CommonAdapter adapter, CommonViewHolder holder, int position) {
                switch (position) {
                    case 0: {
                        wvMinute.setCurrentItem(10);
                        wvHour.setCurrentItem(0);
                        break;
                    }
                    case 1: {
                        wvMinute.setCurrentItem(30);
                        wvHour.setCurrentItem(0);
                        break;
                    }
                    case 2: {
                        wvMinute.setCurrentItem(0);
                        wvHour.setCurrentItem(1);
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }

            @Override
            public void onItemChildClick(CommonAdapter adapter, CommonViewHolder holder, int viewId, int position) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        int hour = wvHour.getCurrentItem();
        int minute = wvMinute.getCurrentItem();
        if (hour == 0 && minute == 0) {
            SnackUtil.snack(btnConfirm, "未设置倒计时");
            return;
        }
        //05:43:22
        long triggerTimeMillis = System.currentTimeMillis() + (hour * 60 + minute) * 60 * 1000;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String triggerTime = sdf.format(new Date(triggerTimeMillis));

        PlanBean planBean = new PlanBean()
                .setId(UUID.randomUUID().toString())
                .setEnable(true)
                .setTriggerTime(triggerTime)
                .setStatus("0000")
                .setDeviceName(dc1Bean.getNames() == null || dc1Bean.getNames().isEmpty() ? "" : dc1Bean.getNames().get(0))
                .setRepeat(PlanBean.REPEAT_ONCE)
                .setDeviceId(dc1Bean.getId())
                .setCommand("0")
                .setSwitchIndex(wvSwitch.getCurrentItem() + "")
                .setRepeatData("");


        WebService.enqueue(WebService.get().addPlan(SpManager.getString(Const.KEY_TOKEN), planBean), new IHttpCallback<String>() {
            @Override
            public void onSuccess(@Nullable String data) {
                EventBus.getDefault().post(new Event().setCode(Event.CODE_ADD_PLAN).setData(planBean));
                getActivity().onBackPressed();
            }

            @Override
            public void onFailure(String message) {
                SnackUtil.snack(btnConfirm, message);
            }
        });

        String status = dc1Bean.getStatus();
        int index = wvSwitch.getCurrentItem();
        char charAt = status.charAt(index);
        if (charAt == '0') {
            //当前关闭的话就开启
            StringBuilder sb = new StringBuilder(status).replace(index, index + 1, "1");
            if (index != 0) {
                sb = sb.replace(0, 1, "1");
            }
            ConnectApi.switchDc1Status(dc1Bean.getId(), sb.toString());
        }
    }

    private void initNames() {
        if (dc1Bean != null) {
            //开关名称及状态
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
                            switchIndexList.add("分控" + (i - 1));
                        }
                    } else {
                        switchIndexList.add(str);
                    }
                }
            } else {
                switchIndexList = new ArrayList<>();
                switchIndexList.add("总开关");
                switchIndexList.add("分控1");
                switchIndexList.add("分控2");
                switchIndexList.add("分控3");
            }
            StringWheelAdapter switchAdapter = new StringWheelAdapter(switchIndexList);
            wvSwitch.setCyclic(false);
            wvSwitch.setCurrentItem(0);
            wvSwitch.setTypeface(Typeface.DEFAULT);
            wvSwitch.setTextColorCenter(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            wvSwitch.setAdapter(switchAdapter);
        }
    }

    public static class RvAdapter extends CommonAdapter<String> {

        private int mSelectionPos = -1;

        @Override
        public int initLayoutId() {
            return R.layout.item_count_down;
        }

        @Override
        public void onBind(CommonViewHolder holder, int position) {
            holder.setText(R.id.tv, getData().get(position))
                    .setTextColorByResId(R.id.tv, mSelectionPos == position ? R.color.colorPrimary : R.color.textPrimary)
                    .setVisibility(R.id.iv, mSelectionPos == position ? View.VISIBLE : View.INVISIBLE)
                    .setOnItemClickListener();
        }

        @Override
        protected void onItemClicked(CommonViewHolder holder) {
            super.onItemClicked(holder);
            int adapterPosition = holder.getAdapterPosition();
            int pre = mSelectionPos;
            mSelectionPos = adapterPosition;
            notifyItemChanged(pre);
            notifyItemChanged(mSelectionPos);
        }

        public int getSelectionPos() {
            return mSelectionPos;
        }
    }
}
