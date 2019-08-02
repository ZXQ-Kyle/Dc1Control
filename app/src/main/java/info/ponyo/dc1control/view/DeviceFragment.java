package info.ponyo.dc1control.view;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import info.ponyo.dc1control.R;
import info.ponyo.dc1control.base.CommonAdapter;
import info.ponyo.dc1control.base.CommonViewHolder;
import info.ponyo.dc1control.base.OnRecyclerViewItemClickListener;
import info.ponyo.dc1control.bean.Dc1Bean;
import info.ponyo.dc1control.socket.Connection;
import info.ponyo.dc1control.socket.ConnectionManager;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author zxq
 */
public class DeviceFragment extends Fragment implements OnRecyclerViewItemClickListener {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout srl;

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
        initView();
    }

    private void initView() {
        srl.setOnRefreshListener(() -> {
            Connection.getInstance().appendMsgToQueue("query");
            srl.postDelayed(() -> srl.setRefreshing(false), 500);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RvAdapter mAdapter = new RvAdapter();
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        ConnectionManager.getInstance().setListener(dc1Beans -> {
            getActivity().runOnUiThread(() -> mAdapter.setData(dc1Beans));
        });
        Connection.getInstance().appendMsgToQueue("query");
    }

    @Override
    public void onItemClick(CommonAdapter adapter, CommonViewHolder holder, int position) {

    }

    @Override
    public void onItemChildClick(CommonAdapter adapter, CommonViewHolder holder, int viewId, int position) {

    }

    private static class RvAdapter extends CommonAdapter<Dc1Bean> {

        @Override
        public int initLayoutId() {
            return R.layout.item_dc1;
        }

        @Override
        public void onBind(CommonViewHolder holder, int position) {
            Dc1Bean bean = getData().get(position);
            holder.setText(R.id.tv_info, String.format("电压:%d  电流:%d  功率:%d", bean.getV(), bean.getI(), bean.getP()));
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
                Connection.getInstance().appendMsgToQueue("set id=" + dc1Bean.getId() + " status=" + sb.toString());
            };

            sb1.setOnCheckedChangeListener(changeListener);
            sb2.setOnCheckedChangeListener(changeListener);
            sb3.setOnCheckedChangeListener(changeListener);
            sb4.setOnCheckedChangeListener(changeListener);
        }
    }
}
