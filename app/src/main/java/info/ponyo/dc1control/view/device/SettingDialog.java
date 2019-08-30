package info.ponyo.dc1control.view.device;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.util.Consumer;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import info.ponyo.dc1control.R;
import info.ponyo.dc1control.socket.ConnectionManager;
import info.ponyo.dc1control.util.Const;
import info.ponyo.dc1control.util.SpManager;

/**
 * @author zxq
 * @Date 2019/8/14.
 * @Description:
 */
public class SettingDialog extends AppCompatDialogFragment {

    private AppCompatEditText mEtHost;
    private AppCompatButton mBtnConfirm;
    private AppCompatButton mBtnCancel;
    private Consumer mOnConfirmClickListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_edit_host, container);
        mEtHost = view.findViewById(R.id.et_host);
        mBtnConfirm = view.findViewById(R.id.btn_confirm);
        mBtnCancel = view.findViewById(R.id.btn_cancel);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.85), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        mEtHost.setText(SpManager.getString(Const.KEY_HOST, "192.168.1.1") + ":" + SpManager.getInt(Const.KEY_PORT, 8800));
        mBtnCancel.setOnClickListener(v -> dismiss());
        mBtnConfirm.setOnClickListener(v -> {
            String trim = mEtHost.getText().toString().trim();
            if (TextUtils.isEmpty(trim)) {
                mEtHost.setError("服务器地址不能为空");
                return;
            }
            String[] strings = trim.split(":");
            if (strings.length == 1) {
                SpManager.putString(Const.KEY_HOST, strings[0]);
                SpManager.putInt(Const.KEY_PORT, 80);
            } else if (strings.length == 2) {
                SpManager.putString(Const.KEY_HOST, strings[0]);
                try {
                    SpManager.putInt(Const.KEY_PORT, Integer.parseInt(strings[1]));
                } catch (NumberFormatException e) {
                    mEtHost.setError("输入格式错误");
                    return;
                }
            } else {
                mEtHost.setError("输入格式错误");
                return;
            }
            ConnectionManager.getInstance().reset();
            if (mOnConfirmClickListener != null) {
                mOnConfirmClickListener.accept(null);
            }
            dismiss();
        });
    }

    public DialogFragment setOnConfirmClickListener(Consumer consumer) {
        mOnConfirmClickListener = consumer;
        return this;
    }
}
