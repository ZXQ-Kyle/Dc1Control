package info.ponyo.dc1control.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

/**
 * @author zxq
 * @Date 2019/8/30.
 * @Description:
 */
public class SnackUtil {
    public static final void setBackground(View view, Drawable drawable) {
        view.setBackground(drawable);
    }

    public static final Drawable getDrawable(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getDrawable(id);
        } else {
            return context.getResources().getDrawable(id);
        }
    }

    public static Snackbar snack(View anchor, String msg) {
        Snackbar snackbar = Snackbar.make(anchor, msg, Snackbar.LENGTH_LONG);
        snackbar.show();
        return snackbar;
    }
}
