package info.ponyo.dc1control.view.device;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.annimon.stream.function.Consumer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import info.ponyo.dc1control.R;
import info.ponyo.dc1control.base.CommonAdapter;
import info.ponyo.dc1control.base.CommonViewHolder;
import info.ponyo.dc1control.base.OnRecyclerViewItemClickListener;
import info.ponyo.dc1control.bean.HostBean;
import info.ponyo.dc1control.network.http.WebService;
import info.ponyo.dc1control.network.socket.ConnectApi;
import info.ponyo.dc1control.network.socket.ConnectionManager;
import info.ponyo.dc1control.util.Const;
import info.ponyo.dc1control.util.MD5;
import info.ponyo.dc1control.util.SpManager;

/**
 * @author zxq
 * @Date 2019/8/14.
 * @Description:
 */
public class SettingDialog extends AppCompatDialogFragment {

    private AppCompatEditText mEtHost, mEtToken, mTcpPort, mHttpPort;
    private AppCompatButton mBtnConfirm;
    private AppCompatButton mBtnCancel;
    private RecyclerView mRv;
    private RvAdapter mAdapter;
    private Consumer mOnConfirmClickListener;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Window window = getDialog().getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        super.onActivityCreated(savedInstanceState);
        window.setBackgroundDrawableResource(R.color.white);
        DisplayMetrics dm = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        } else {
            window.getWindowManager().getDefaultDisplay().getMetrics(dm);
        }
        //设置弹窗大小为会屏
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dm.heightPixels);
        //去除阴影
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.dimAmount = 0.0f;
        window.setAttributes(layoutParams);
    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_edit_host, container);
        mEtHost = view.findViewById(R.id.et_host);
        mTcpPort = view.findViewById(R.id.et_tcp_port);
        mHttpPort = view.findViewById(R.id.et_http_port);
        mEtToken = view.findViewById(R.id.et_token);
        mBtnConfirm = view.findViewById(R.id.btn_confirm);
        mBtnCancel = view.findViewById(R.id.btn_cancel);
        mRv = view.findViewById(R.id.rv);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
//        Dialog dialog = getDialog();
//        if (dialog != null) {
//            DisplayMetrics dm = new DisplayMetrics();
//            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
//            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.85), ViewGroup.LayoutParams.WRAP_CONTENT);
//        }
        mEtHost.setText(SpManager.getString(Const.KEY_HOST, "192.168.1.1"));
        mTcpPort.setText(SpManager.getString(Const.KEY_TCP_PORT, "8800"));
        mHttpPort.setText(SpManager.getString(Const.KEY_HTTP_PORT, "8880"));
        mEtToken.setText(SpManager.getString(Const.KEY_TOKEN, ""));
        mBtnCancel.setOnClickListener(v -> dismiss());
        mBtnConfirm.setOnClickListener(v -> {
            String host = mEtHost.getText().toString().trim();
            String tcpPort = mTcpPort.getText().toString().trim();
            String httpPort = mHttpPort.getText().toString().trim();
            String token = mEtToken.getText().toString().trim();
            if (TextUtils.isEmpty(host)) {
                mEtHost.setError("服务器地址不能为空");
                return;
            }
            if (TextUtils.isEmpty(token)) {
                token = "dc1server";
            }
            String md5 = MD5.getMD5(token);
            saveHistory(host, tcpPort, httpPort, md5);
            saveAndReset(host, tcpPort, httpPort, md5);
        });

        mRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new RvAdapter();
        String string = SpManager.getString(Const.KEY_HISTORY);
        if (!TextUtils.isEmpty(string)) {
            List<HostBean> list = new Gson().fromJson(string, new TypeToken<List<HostBean>>() {
            }.getType());
            mAdapter.setData(list);
        }
        mRv.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(CommonAdapter adapter, CommonViewHolder holder, int position) {
                HostBean hostBean = mAdapter.getData().get(position);
                saveAndReset(hostBean.getHost(), hostBean.getSocketPort(), hostBean.getHttpPort(), hostBean.getToken());
            }

            @Override
            public void onItemChildClick(CommonAdapter adapter, CommonViewHolder holder, int viewId, int position) {
                List<HostBean> data = mAdapter.getData();
                data.remove(position);
                mAdapter.notifyDataSetChanged();
                String toJson = new Gson().toJson(data);
                SpManager.putString(Const.KEY_HISTORY, toJson);
            }
        });
    }

    private void saveAndReset(String trim, String tcpPort, String httpPort, String token) {
        SpManager.putString(Const.KEY_HOST, trim);
        SpManager.putString(Const.KEY_TCP_PORT, tcpPort);
        SpManager.putString(Const.KEY_HTTP_PORT, httpPort);
        SpManager.putString(Const.KEY_TOKEN, token);
        ConnectApi.token = token;
        ConnectionManager.getInstance().reset();
        WebService.createApi();
        if (mOnConfirmClickListener != null) {
            mOnConfirmClickListener.accept(null);
        }
        dismiss();
    }

    private void saveHistory(String host, String tcpPort, String httpPort, String token) {
        HostBean hostBean = new HostBean()
                .setHost(host)
                .setSocketPort(tcpPort)
                .setHttpPort(httpPort)
                .setToken(token);
        List<HostBean> data = mAdapter.getData();
        if (data == null) {
            data = new ArrayList<>();
        }
        data.add(0, hostBean);
        if (data.size() > 5) {
            data.remove(data.size() - 1);
        }
        String toJson = new Gson().toJson(data);
        SpManager.putString(Const.KEY_HISTORY, toJson);
    }

    public SettingDialog setOnConfirmClickListener(Consumer consumer) {
        mOnConfirmClickListener = consumer;
        return this;
    }

    private static class RvAdapter extends CommonAdapter<HostBean> {

        @Override
        public int initLayoutId() {
            return R.layout.item_text;
        }

        @Override
        public void onBind(CommonViewHolder holder, int position) {
            HostBean hostBean = getData().get(position);
            String sb = hostBean.getHost() +
                    " | " +
                    hostBean.getSocketPort() +
                    " | " +
                    hostBean.getHttpPort();
            holder.setText(R.id.tv, sb)
                    .setBackgroundResource(R.id.root, position % 2 == 0 ? R.color.gray : R.color.white)
                    .setOnItemChildClickListener(R.id.iv_delete)
                    .setOnItemClickListener();
        }
    }
}
