package info.ponyo.dc1control.base;


import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用RecyclerView的adapter<br/>
 * Created by zxq on 2017/9/21.
 */

public abstract class CommonAdapter<T> extends RecyclerView.Adapter<CommonViewHolder> {

    private List<T> mList = new ArrayList<>();
    private OnRecyclerViewItemClickListener mListener;
    private OnRecyclerViewItemLongClickListener mLongListener;

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return CommonViewHolder.newInstance(parent, initLayoutId(), this);
    }


    public abstract @LayoutRes
    int initLayoutId();

    @Override
    public void onBindViewHolder(CommonViewHolder holder, int position) {
        onBind(holder, position);
    }

    public abstract void onBind(CommonViewHolder holder, int position);

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    /**
     * 设置整个数据集<br/>
     */
    public void setData(List<T> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public List<T> getData() {
        return mList;
    }

    /**
     * 设置点击监听
     */
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mListener = listener;
    }

    public OnRecyclerViewItemClickListener getItemClickListener() {
        return mListener;
    }

    /**
     * 设置长按监听
     */
    public void setOnItemLongClickListener(OnRecyclerViewItemLongClickListener listener) {
        mLongListener = listener;
    }

    public OnRecyclerViewItemLongClickListener getItemLongClickListener() {
        return mLongListener;
    }


    /**
     * 根视图点击时被调用<br/>
     * 在接口方法之前调用
     *
     * @param holder 被点击的holder
     */
    protected void onItemClicked(CommonViewHolder holder) {

    }

    /**
     * 根视图长按时被调用<br/>
     * 在接口方法之前调用
     *
     * @param holder 被点击的holder
     */
    protected void onItemLongClicked(CommonViewHolder holder) {

    }

    /**
     * 子视图点击时被调用<br/>
     * 在接口方法之前调用
     *
     * @param holder 被点击的holder
     */
    protected void onChildItemClicked(CommonViewHolder holder, int viewId) {

    }
}
