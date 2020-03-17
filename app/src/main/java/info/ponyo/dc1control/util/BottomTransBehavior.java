package info.ponyo.dc1control.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import info.ponyo.dc1control.R;

/**
 * @author zxq
 * @Date 2019/11/4.
 * @Description:
 */
public class BottomTransBehavior extends FloatingActionButton.Behavior {

    private boolean animating;

    public BottomTransBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                       @NonNull FloatingActionButton child,
                                       @NonNull View directTargetChild,
                                       @NonNull View target,
                                       int axes,
                                       int type) {
        // 确保滚动方向为垂直方向
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        if ((dyConsumed > 0 || dyUnconsumed > 0) && child.getVisibility() == View.VISIBLE && !animating) {
            // 手指上滑
            transHide(child);
        } else if ((dyConsumed < 0 || dyUnconsumed < 0) && child.getVisibility() != View.VISIBLE) {
            // 手指下滑
            transShow(child);
        }
    }

    private void transShow(final View view) {
        Animation up = AnimationUtils.loadAnimation(view.getContext(), R.anim.trans_up);
        up.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
                view.setEnabled(true);
                animating = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animating = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(up);
    }

    private void transHide(final View view) {
        Animation down = AnimationUtils.loadAnimation(view.getContext(), R.anim.trans_down);
        down.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                animating = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.INVISIBLE);
                view.setEnabled(false);
                animating = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(down);
    }
}
