package info.ponyo.dc1control.view.device;


import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.avos.avoscloud.feedback.FeedbackAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import info.ponyo.dc1control.R;
import info.ponyo.dc1control.base.CommonAdapter;
import info.ponyo.dc1control.base.CommonViewHolder;
import info.ponyo.dc1control.base.OnRecyclerViewItemClickListener;
import info.ponyo.dc1control.bean.Dc1Bean;
import info.ponyo.dc1control.network.http.IHttpCallback;
import info.ponyo.dc1control.network.http.WebService;
import info.ponyo.dc1control.network.socket.ConnectApi;
import info.ponyo.dc1control.network.socket.ConnectionManager;
import info.ponyo.dc1control.util.Const;
import info.ponyo.dc1control.util.Event;
import info.ponyo.dc1control.util.SnackUtil;
import info.ponyo.dc1control.util.SpManager;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author zxq
 */
public class DeviceFragment extends Fragment implements OnRecyclerViewItemClickListener {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout srl;
    private DeviceAdapter mAdapter;

    public static DeviceFragment newInstance() {
        DeviceFragment fragment = new DeviceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rv);
        srl = view.findViewById(R.id.srl);
    }

    @Override
    public void onStart() {
        super.onStart();
        setHasOptionsMenu(true);
        initView();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList("devices", (ArrayList<? extends Parcelable>) mAdapter.getData());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        ConnectionManager.getInstance().reset();
        if (savedInstanceState != null) {
            ArrayList<Dc1Bean> devices = savedInstanceState.getParcelableArrayList("devices");
            if (devices != null && mAdapter != null) {
                mAdapter.setData(devices);
            }
        }
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_setting, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_setting) {
            new SettingDialog()
                    .setOnConfirmClickListener(o -> {
                        mAdapter.setData(null);
                        setRefresh();
                    })
                    .show(getActivity().getSupportFragmentManager(), "SettingDialog");
        } else {
            FeedbackAgent agent = new FeedbackAgent(getContext());
            agent.startDefaultThreadActivity();
        }
        return true;
    }

    private void initView() {
        srl.setOnRefreshListener(this::setRefresh);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new DeviceAdapter();
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        setRefresh();
    }

    private void setRefresh() {
        srl.setRefreshing(true);
        WebService.enqueue(WebService.get().queryDeviceList(SpManager.getString(Const.KEY_TOKEN, "")), new IHttpCallback<List<Dc1Bean>>() {
            @Override
            public void onSuccess(@Nullable List<Dc1Bean> data) {
                mAdapter.setData(data);
                srl.setRefreshing(false);
            }

            @Override
            public void onFailure(String message) {
                SnackUtil.snack(recyclerView,message);
                srl.setRefreshing(false);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event event) {
        if (Event.CODE_DEVICE_LIST.equals(event.getCode())) {
            srl.setRefreshing(false);
            mAdapter.setData((List<Dc1Bean>) event.getData());
        }
    }

    @Override
    public void onItemClick(CommonAdapter adapter, CommonViewHolder holder, int position) {

    }

    @Override
    public void onItemChildClick(CommonAdapter adapter, CommonViewHolder holder, int viewId, int position) {
        Dc1Bean dc1Bean = mAdapter.getData().get(position);
        if (viewId == R.id.tv_power_info) {
            showResetPowerDialog(dc1Bean);
        } else if (viewId == R.id.iv_plan) {
            EventBus.getDefault().post(new Event().setCode(Event.CODE_JUMP_TO_PLAN).setData(dc1Bean));
        } else {
            View view = View.inflate(getContext(), R.layout.view_edit_name, null);
            EditText etSwitch = view.findViewById(R.id.et_switch);
            EditText etSwitch1 = view.findViewById(R.id.et_switch_1);
            EditText etSwitch2 = view.findViewById(R.id.et_switch_2);
            EditText etSwitch3 = view.findViewById(R.id.et_switch_3);
            EditText etSwitch4 = view.findViewById(R.id.et_switch_4);

            ArrayList<String> names = dc1Bean.getNames();
            if (names.size() == 5) {
                etSwitch.setText(names.get(0));
                etSwitch1.setText(names.get(1));
                etSwitch2.setText(names.get(2));
                etSwitch3.setText(names.get(3));
                etSwitch4.setText(names.get(4));
            }

            new AlertDialog.Builder(getContext())
                    .setCancelable(false)
                    .setTitle("设置名称")
                    .setView(view)
                    .setPositiveButton("确定", (dialog, which) -> {
                        names.clear();
                        names.add(etSwitch.getText().toString().trim());
                        names.add(etSwitch1.getText().toString().trim());
                        names.add(etSwitch2.getText().toString().trim());
                        names.add(etSwitch3.getText().toString().trim());
                        names.add(etSwitch4.getText().toString().trim());
                        mAdapter.notifyItemChanged(position);
                        ConnectApi.updateDc1Name(dc1Bean.getId(), names);
                        dialog.dismiss();
                    })
                    .setPositiveButtonIcon(getResources().getDrawable(R.drawable.ic_confirm))
                    .setNegativeButton("取消", (dialog1, which) -> dialog1.dismiss())
                    .setNegativeButtonIcon(getResources().getDrawable(R.drawable.ic_cancel))
                    .create()
                    .show();
        }
    }

    private void showResetPowerDialog(Dc1Bean dc1Bean) {
        new AlertDialog.Builder(getContext())
                .setIcon(R.drawable.ic_tip)
                .setTitle("提示")
                .setMessage("用电量每增加0.05kwh更新数据,点击重置重新计算")
                .setCancelable(true)
                .setPositiveButton("重置", (dialog, which) -> {
                    ConnectApi.resetPower(dc1Bean.getId());
                })
                .setPositiveButtonIcon(getResources().getDrawable(R.drawable.ic_confirm))
                .setNegativeButton("取消", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setNegativeButtonIcon(getResources().getDrawable(R.drawable.ic_cancel))
                .show();
    }
}
