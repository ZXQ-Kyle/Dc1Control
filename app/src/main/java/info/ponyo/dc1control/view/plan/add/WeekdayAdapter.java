package info.ponyo.dc1control.view.plan.add;

import java.util.Arrays;

import info.ponyo.dc1control.R;
import info.ponyo.dc1control.base.CommonAdapter;
import info.ponyo.dc1control.base.CommonViewHolder;
import info.ponyo.dc1control.bean.PlanBean;

/**
 * @author zxq
 * @Date 2019/9/17.
 * @Description:
 */
public class WeekdayAdapter extends CommonAdapter<String> {

    private final boolean[] mSelectState;

    public WeekdayAdapter() {
        setData(PlanBean.WEEK_DAY_CN);
        mSelectState = new boolean[7];
    }

    @Override
    public int initLayoutId() {
        return R.layout.item_weekday;
    }

    @Override
    public void onBind(CommonViewHolder holder, int position) {
        holder.setText(R.id.tv_weekday, getData().get(position))
                .setSelected(R.id.tv_weekday, mSelectState[position])
                .setOnItemClickListener();
    }

    public boolean[] getSelectState() {
        return mSelectState;
    }

    public void clearState() {
        Arrays.fill(mSelectState, false);
        notifyDataSetChanged();
    }
}
