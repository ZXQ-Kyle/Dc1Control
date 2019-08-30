package info.ponyo.dc1control.view.plan;

import android.support.v7.widget.SwitchCompat;

import info.ponyo.dc1control.R;
import info.ponyo.dc1control.base.CommonAdapter;
import info.ponyo.dc1control.base.CommonViewHolder;
import info.ponyo.dc1control.bean.PlanBean;
import info.ponyo.dc1control.socket.ConnectApi;

/**
 * @author zxq
 * @Date 2019/8/26.
 * @Description:
 */
public class PlanAdapter extends CommonAdapter<PlanBean> {
    @Override
    public int initLayoutId() {
        return R.layout.item_plan;
    }

    @Override
    public void onBind(CommonViewHolder holder, int position) {
        PlanBean bean = getData().get(position);
        SwitchCompat sw = holder.getView(R.id.sw);
        sw.setOnCheckedChangeListener(null);

        holder.setText(R.id.tv_trigger_status, "触发时间：" + bean.getStatus())
                .setChecked(R.id.sw, bean.isEnable())
                .setText(R.id.tv_trigger_time, "开关指令：" + bean.getTriggerTime())
                .setOnItemClickListener();

        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ConnectApi.enablePlanById(bean.getId(), isChecked);
        });
    }
}
