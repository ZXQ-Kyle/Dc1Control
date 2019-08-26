package info.ponyo.dc1control.view;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;

import info.ponyo.dc1control.R;
import info.ponyo.dc1control.base.CommonAdapter;
import info.ponyo.dc1control.base.CommonViewHolder;
import info.ponyo.dc1control.base.OnRecyclerViewItemClickListener;
import info.ponyo.dc1control.bean.Dc1Bean;
import info.ponyo.dc1control.socket.ConnectApi;
import info.ponyo.dc1control.socket.ConnectionManager;

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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_setting, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        new SettingDialog()
                .setOnConfirmClickListener(o -> {
                    mAdapter.setData(null);
                    recyclerView.postDelayed(ConnectApi::queryDc1List, 100);
                })
                .show(getActivity().getSupportFragmentManager(), "SettingDialog");
        return true;
    }

    private void initView() {
        srl.setOnRefreshListener(() -> {
            mAdapter.setData(null);
            ConnectApi.queryDc1List();
            srl.postDelayed(() -> srl.setRefreshing(false), 500);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new DeviceAdapter();
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        ConnectionManager.getInstance().setListener(dc1Beans -> {
            getActivity().runOnUiThread(() -> mAdapter.setData(dc1Beans));
        });
        recyclerView.post(ConnectApi::queryDc1List);
    }

    @Override
    public void onItemClick(CommonAdapter adapter, CommonViewHolder holder, int position) {

    }

    @Override
    public void onItemChildClick(CommonAdapter adapter, CommonViewHolder holder, int viewId, int position) {
        Dc1Bean dc1Bean = mAdapter.getData().get(position);
        if (viewId == R.id.tv_power_info) {
            showResetPowerDialog(dc1Bean);
        } else {
            View view = View.inflate(getContext(), R.layout.view_edit_name, null);
            EditText etSwitch = view.findViewById(R.id.et_switch);
            EditText etSwitch1 = view.findViewById(R.id.et_switch_1);
            EditText etSwitch2 = view.findViewById(R.id.et_switch_2);
            EditText etSwitch3 = view.findViewById(R.id.et_switch_3);
            EditText etSwitch4 = view.findViewById(R.id.et_switch_4);

            ArrayList<String> names = dc1Bean.getNames();
            if (names != null && names.size() == 5) {
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
                    .setNegativeButton("取消", (dialog1, which) -> {
                        dialog1.dismiss();
                    })
                    .setNegativeButtonIcon(getResources().getDrawable(R.drawable.ic_cancel))
                    .create()
                    .show();
        }
    }

    private void showResetPowerDialog(Dc1Bean dc1Bean) {
        new AlertDialog.Builder(getContext())
                .setIcon(R.drawable.ic_setting)
                .setTitle("提示")
                .setMessage("用电量每增加50kwh更新数据,点击重置重新计算")
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
