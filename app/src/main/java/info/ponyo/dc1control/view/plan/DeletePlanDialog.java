package info.ponyo.dc1control.view.plan;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.util.Consumer;
import androidx.fragment.app.DialogFragment;

import info.ponyo.dc1control.R;

/**
 * @author zxq
 * @Date 2019/8/14.
 * @Description:
 */
public class DeletePlanDialog extends AppCompatDialogFragment {

    private Consumer mOnConfirmClickListener;
    private TextView mBtnConfirm;
    private TextView mBtnCancel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setBackgroundDrawableResource(R.color.transparent);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_delete_plan, container);
        mBtnConfirm = view.findViewById(R.id.btn_confirm);
        mBtnCancel = view.findViewById(R.id.btn_cancel);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            //得到LayoutParams
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            //修改gravity
            params.gravity = Gravity.BOTTOM;
            params.width = (int) (window.getWindowManager().getDefaultDisplay().getWidth()*0.99);
            window.getDecorView().setPadding(0,0,0,8);
            window.setAttributes(params);

        }
        mBtnCancel.setOnClickListener(v -> dismiss());
        if (mOnConfirmClickListener != null) {
            mBtnConfirm.setOnClickListener(v -> {
                mOnConfirmClickListener.accept(null);
                dismiss();
            });
        }
    }

    public DialogFragment setOnConfirmClickListener(Consumer consumer) {
        mOnConfirmClickListener = consumer;
        return this;
    }
}
