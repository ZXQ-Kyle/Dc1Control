package info.ponyo.dc1control.base;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by zxq on 2017/12/6.
 * Description:
 */
public class CommonViewHolder extends RecyclerView.ViewHolder {

    private CommonAdapter mAdapter;
    private final SparseArrayCompat<View> mViews = new SparseArrayCompat<>();
    private final OnClickListenerImp mListener = new OnClickListenerImp();
    private OnChildClickListenerImp mChildListener;
    private final OnLongClickListenerImp mLongListener = new OnLongClickListenerImp();
    private Object mTag;
    private final Context mContext;

    public CommonViewHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext().getApplicationContext();
    }

    public CommonViewHolder(View itemView, CommonAdapter adapter) {
        super(itemView);
        mContext = itemView.getContext().getApplicationContext();
        mAdapter = adapter;
    }

    public static CommonViewHolder newInstance(ViewGroup parent, int mLayoutId, CommonAdapter adapter) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(mLayoutId, parent, false);
        return new CommonViewHolder(view, adapter);
    }

    public <V extends View> V getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (V) view;
    }

    /**
     * @return 返回ApplicationContext
     */
    public Context getContext() {
        return mContext;
    }

    public String getString(@StringRes int resId) {
        if (null == mContext) {
            return "";
        }
        return mContext.getString(resId);
    }

    public CommonViewHolder setBackgroundColor(@IdRes int viewId, @ColorRes int color) {
        View view = getView(viewId);
        if (null != view) {
            view.setBackgroundColor(getContext().getResources().getColor(color));
        }
        return this;
    }

    public CommonViewHolder setBackgroundResource(@IdRes int viewId, @DrawableRes int resid) {
        View view = getView(viewId);
        if (null != view) {
            view.setBackgroundResource(resid);
        }
        return this;
    }

    public CommonViewHolder setText(@IdRes int viewId, CharSequence text) {
        TextView tv = getView(viewId);
        if (null != tv) {
            tv.setText(text);
        }
        return this;
    }

    public CommonViewHolder setText(@IdRes int viewId, @StringRes int strId) {
        return setText(viewId, getContext().getString(strId));
    }

    public CommonViewHolder setTextSize(@IdRes int viewId, int spSize) {
        TextView tv = getView(viewId);
        if (null != tv) {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, spSize);
        }
        return this;
    }

    public CommonViewHolder setChecked(@IdRes int viewId, boolean isChecked) {
        Checkable impl = getView(viewId);
        if (null != impl) {
            impl.setChecked(isChecked);
        }
        return this;
    }

    public CommonViewHolder setSelected(@IdRes int viewId, boolean isSelected) {
        View view = getView(viewId);
        if (null != view) {
            view.setSelected(isSelected);
        }
        return this;
    }

    public CommonViewHolder setTextColor(@IdRes int viewId, @ColorInt int color) {
        TextView tv = getView(viewId);
        if (null != tv) {
            tv.setTextColor(color);
        }
        return this;
    }

    public CommonViewHolder setTextColorByResId(@IdRes int viewId, @ColorRes int colorRes) {
        TextView tv = getView(viewId);
        if (null != tv) {
            tv.setTextColor(ContextCompat.getColor(mContext, colorRes));
        }
        return this;
    }

    /**
     * 设置图片背景setImageResource(资源id);
     *
     * @param viewId viewId
     * @param imgId  资源id
     * @return
     */
    public CommonViewHolder setImage(@IdRes int viewId, @DrawableRes int imgId) {
        ImageView iv = getView(viewId);
        if (null != iv) {
            iv.setImageResource(imgId);
        }
        return this;
    }

    public CommonViewHolder setImageDrawable(@IdRes int viewId, Drawable drawable) {
        ImageView iv = getView(viewId);
        if (null != iv) {
            iv.setImageDrawable(drawable);
        }
        return this;
    }

    /**
     * @param viewId     viewId
     * @param visibility {@link View#GONE} , {@link View#VISIBLE} , {@link View#INVISIBLE}
     */
    public CommonViewHolder setVisibility(@IdRes int viewId, int visibility) {
        View view = getView(viewId);
        if (null != view) {
            view.setVisibility(visibility);
        }
        return this;
    }

    public Object getTag() {
        return mTag;
    }

    public void setTag(Object tag) {
        this.mTag = tag;
    }


    /**
     * 设置子视图点击事件
     *
     * @param viewId 被点击的子控件的viewId
     */
    public CommonViewHolder setOnItemChildClickListener(@IdRes final int viewId) {
        View view = getView(viewId);
        if (view != null) {
            if (!view.isClickable()) {
                view.setClickable(true);
            }
            if (mChildListener == null) {
                mChildListener = new OnChildClickListenerImp();
            }
            view.setOnClickListener(mChildListener);
        }
        return this;
    }

    private class OnChildClickListenerImp implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if (pos < 0 || pos >= mAdapter.getItemCount()) {
                return;
            }
            mAdapter.onChildItemClicked(CommonViewHolder.this, v.getId());
            if (mAdapter.getItemClickListener() != null) {
                mAdapter.getItemClickListener().onItemChildClick(mAdapter, CommonViewHolder.this, v.getId(), getLayoutPosition());
            }
        }
    }

    /**
     * 设置根视图点击事件
     */
    public CommonViewHolder setOnItemClickListener() {
        if (!itemView.isClickable()) {
            itemView.setClickable(true);
        }
        itemView.setOnClickListener(mListener);
        return this;
    }

    private class OnClickListenerImp implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if (pos < 0 || pos >= mAdapter.getItemCount()) {
                return;
            }
            mAdapter.onItemClicked(CommonViewHolder.this);
            if (mAdapter.getItemClickListener() != null) {
                mAdapter.getItemClickListener().onItemClick(mAdapter, CommonViewHolder.this, pos);
            }
        }
    }

    /**
     * 设置根视图长按事件
     */
    public CommonViewHolder setOnItemLongClickListener() {
        if (!itemView.isClickable()) {
            itemView.setClickable(true);
        }
        itemView.setOnLongClickListener(mLongListener);
        return this;
    }

    private class OnLongClickListenerImp implements View.OnLongClickListener {

        @Override
        public boolean onLongClick(View v) {
            int pos = getAdapterPosition();
            if (pos < 0 || pos >= mAdapter.getItemCount()) {
                return false;
            }
            mAdapter.onItemLongClicked(CommonViewHolder.this);
            if (mAdapter.getItemLongClickListener() != null) {
                mAdapter.getItemLongClickListener().onItemLongClick(mAdapter, CommonViewHolder.this, getLayoutPosition());
            }
            return true;
        }
    }
}
