package info.ponyo.dc1control.view;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
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
    private RvAdapter mAdapter;

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
                    ConnectApi.queryDc1List();
                })
                .show(getActivity().getSupportFragmentManager(), "SettingDialog");
        return true;
    }

    private void initView() {
        srl.setOnRefreshListener(() -> {
            ConnectApi.queryDc1List();
            srl.postDelayed(() -> srl.setRefreshing(false), 500);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new RvAdapter();
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        ConnectionManager.getInstance().setListener(dc1Beans -> {
            getActivity().runOnUiThread(() -> mAdapter.setData(dc1Beans));
        });
        ConnectApi.queryDc1List();
    }

    @Override
    public void onItemClick(CommonAdapter adapter, CommonViewHolder holder, int position) {

    }

    @Override
    public void onItemChildClick(CommonAdapter adapter, CommonViewHolder holder, int viewId, int position) {
        Dc1Bean dc1Bean = mAdapter.getData().get(position);
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
                .setNegativeButton("取消", (dialog1, which) -> {
                    dialog1.dismiss();
                })
                .create()
                .show();
    }

    private static class RvAdapter extends CommonAdapter<Dc1Bean> {

        @Override
        public int initLayoutId() {
            return R.layout.item_dc1;
        }

        @Override
        public void onBind(CommonViewHolder holder, int position) {
            Dc1Bean bean = getData().get(position);
            holder.setText(R.id.tv_info, String.format("电压:%d  电流:%d  功率:%d", bean.getV(), bean.getI(), bean.getP()))
                    .setOnItemChildClickListener(R.id.iv_edit);

            ArrayList<String> names = bean.getNames();
            if (names != null && names.size() == 5) {
                holder.setText(R.id.tv_name, TextUtils.isEmpty(names.get(0)) ? "插排" : names.get(0))
                        .setText(R.id.tv_1, TextUtils.isEmpty(names.get(1)) ? "1、总开关" : "1、" + names.get(1))
                        .setText(R.id.tv_2, TextUtils.isEmpty(names.get(2)) ? "2、开关" : "2、" + names.get(2))
                        .setText(R.id.tv_3, TextUtils.isEmpty(names.get(3)) ? "3、开关" : "3、" + names.get(3))
                        .setText(R.id.tv_4, TextUtils.isEmpty(names.get(4)) ? "4、开关" : "4、" + names.get(4));
            }

            String status = bean.getStatus();
            SwitchCompat sb1 = holder.getView(R.id.sb_1);
            SwitchCompat sb2 = holder.getView(R.id.sb_2);
            SwitchCompat sb3 = holder.getView(R.id.sb_3);
            SwitchCompat sb4 = holder.getView(R.id.sb_4);

            sb1.setOnCheckedChangeListener(null);
            sb2.setOnCheckedChangeListener(null);
            sb3.setOnCheckedChangeListener(null);
            sb4.setOnCheckedChangeListener(null);

            sb1.setChecked(status.charAt(0) == '1');
            sb2.setChecked(status.charAt(1) == '1');
            sb3.setChecked(status.charAt(2) == '1');
            sb4.setChecked(status.charAt(3) == '1');

            CompoundButton.OnCheckedChangeListener changeListener = (buttonView, isChecked) -> {
                int pos;
                switch (buttonView.getId()) {
                    case R.id.sb_1: {
                        pos = 0;
                        break;
                    }
                    case R.id.sb_2: {
                        pos = 1;
                        break;
                    }
                    case R.id.sb_3: {
                        pos = 2;
                        break;
                    }
                    case R.id.sb_4:
                    default: {
                        pos = 3;
                    }
                }
                int adapterPosition = holder.getAdapterPosition();
                Dc1Bean dc1Bean = getData().get(adapterPosition);
                String s = dc1Bean.getStatus();
                StringBuilder sb = new StringBuilder(s);
                sb.replace(pos, pos + 1, isChecked ? "1" : "0");
                dc1Bean.setStatus(sb.toString());
                ConnectApi.switchDc1Status(dc1Bean.getId(), sb.toString());
            };

            sb1.setOnCheckedChangeListener(changeListener);
            sb2.setOnCheckedChangeListener(changeListener);
            sb3.setOnCheckedChangeListener(changeListener);
            sb4.setOnCheckedChangeListener(changeListener);
        }
    }
}
