package info.ponyo.dc1control.view.plan;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

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

/**
 * A simple {@link Fragment} subclass.
 *
 * @author zxq
 */
public class PlanFragment extends Fragment implements OnRecyclerViewItemClickListener {
    private Dc1Bean dc1Bean;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout srl;
    private PlanAdapter mAdapter;
    private FloatingActionButton fabAdd;

    public static PlanFragment newInstance() {
        PlanFragment fragment = new PlanFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_plan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rv);
        srl = view.findViewById(R.id.srl);
        fabAdd = view.findViewById(R.id.fab_add);
        initView();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            refresh();
        }
    }

    private void refresh() {
        if (dc1Bean == null) {
            SnackUtil.snack(recyclerView, "无法获取设备Id");
            return;
        }
        srl.setRefreshing(true);
        WebService.enqueue(WebService.get().queryPlanList(SpManager.getString(Const.KEY_TOKEN), dc1Bean.getId()),
                new IHttpCallback<List<PlanBean>>() {
                    @Override
                    public void onSuccess(@Nullable List<PlanBean> data) {
                        mAdapter.setDc1Bean(dc1Bean).setData(data);
                        srl.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(String message) {
                        SnackUtil.snack(recyclerView, message);
                        srl.setRefreshing(false);
                    }
                });
    }

    private void initView() {
        fabAdd.setOnClickListener(v -> {
            EventBus.getDefault().post(new Event().setCode(Event.CODE_JUMP_TO_ADD_PLAN).setData(dc1Bean));
        });
        srl.setOnRefreshListener(() -> {
            mAdapter.setDc1Bean(null).setData(null);
            refresh();
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), LinearLayout.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        mAdapter = new PlanAdapter();
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event event) {
        switch (event.getCode()) {
            case Event.CODE_ADD_PLAN: {
                List<PlanBean> data = mAdapter.getData();
                data.add((PlanBean) event.getData());
                mAdapter.notifyItemInserted(data.size() - 1);
                break;
            }
            case Event.CODE_PLAN_CHANGED: {
                String planId = (String) event.getData();
                List<PlanBean> data = mAdapter.getData();
                for (int i = 0; i < data.size(); i++) {
                    if (TextUtils.equals(data.get(i).getId(), planId)) {
                        data.get(i).setEnable(false);
                        mAdapter.notifyItemChanged(i);
                        return;
                    }
                }
            }
            default: {
                break;
            }
        }
    }

    @Override
    public void onItemClick(CommonAdapter adapter, CommonViewHolder holder, int position) {
        PlanBean planBean = mAdapter.getData().get(position);
        new DeletePlanDialog()
                .setOnConfirmClickListener(o -> {
                    ConnectApi.deletePlanById(planBean.getId());
                    mAdapter.getData().remove(position);
                    mAdapter.notifyItemRemoved(position);
                })
                .show(getActivity().getSupportFragmentManager(), DeletePlanDialog.class.getSimpleName());
    }

    @Override
    public void onItemChildClick(CommonAdapter adapter, CommonViewHolder holder, int viewId, int position) {

    }

    public PlanFragment setDc1Bean(Dc1Bean dc1Bean) {
        if (this.dc1Bean != dc1Bean && mAdapter != null && mAdapter.getData() != null) {
            mAdapter.setDc1Bean(dc1Bean);
            mAdapter.setData(null);
        }
        this.dc1Bean = dc1Bean;
        return this;
    }
}
