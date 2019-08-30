package info.ponyo.dc1control.view.plan;


import android.os.Bundle;
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
import info.ponyo.dc1control.socket.ConnectApi;
import info.ponyo.dc1control.util.Event;

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
        fabAdd.setOnClickListener(v -> {
            EventBus.getDefault().post(new Event().setCode(Event.CODE_JUMP_TO_ADD_PLAN).setData(dc1Bean));
        });
        srl.setOnRefreshListener(() -> {
            mAdapter.setData(null);
            if (dc1Bean != null) {
                ConnectApi.queryPlanList(dc1Bean.getId());
            }
            srl.postDelayed(() -> srl.setRefreshing(false), 500);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));
        mAdapter = new PlanAdapter();
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        if (dc1Bean != null) {
            recyclerView.post(() -> ConnectApi.queryPlanList(dc1Bean.getId()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event event) {
        if (Event.CODE_PLAN_LIST.equals(event.getCode())) {
            mAdapter.setData((List<PlanBean>) event.getData());
        } else if (Event.CODE_ADD_PLAN.equals(event.getCode())) {
            List<PlanBean> data = mAdapter.getData();
            data.add((PlanBean) event.getData());
            mAdapter.notifyItemInserted(data.size() - 1);
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
        this.dc1Bean = dc1Bean;
        return this;
    }

}
