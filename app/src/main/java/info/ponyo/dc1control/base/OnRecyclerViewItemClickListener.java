package info.ponyo.dc1control.base;

/**
 * Created by zxq on 2017/12/6.
 * Description:
 */

public interface OnRecyclerViewItemClickListener {

    void onItemClick(CommonAdapter adapter, CommonViewHolder holder, int position);

    void onItemChildClick(CommonAdapter adapter, CommonViewHolder holder, int viewId, int position);
}
