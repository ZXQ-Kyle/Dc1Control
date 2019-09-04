package info.ponyo.dc1control.view.device;

import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;

import androidx.appcompat.widget.SwitchCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import info.ponyo.dc1control.R;
import info.ponyo.dc1control.base.CommonAdapter;
import info.ponyo.dc1control.base.CommonViewHolder;
import info.ponyo.dc1control.bean.Dc1Bean;
import info.ponyo.dc1control.socket.ConnectApi;

/**
 * @author zxq
 * @Date 2019/8/26.
 * @Description:
 */
public class DeviceAdapter extends CommonAdapter<Dc1Bean> {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());

    @Override
    public int initLayoutId() {
        return R.layout.item_dc1;
    }

    @Override
    public void onBind(CommonViewHolder holder, int position) {
        Dc1Bean bean = getData().get(position);

        holder.setBackgroundColor(R.id.root, bean.isOnline() ? R.color.transparent : R.color.darkGray)
                .setVisibility(R.id.iv_offline, bean.isOnline() ? View.GONE : View.VISIBLE)
                .setText(R.id.tv_info, String.format(Locale.getDefault(),
                        "电压:%dV  电流:%dmA  功率:%dW", bean.getV(), bean.getI(), bean.getP()))
                .setOnItemChildClickListener(R.id.iv_edit)
                .setOnItemChildClickListener(R.id.iv_plan)
                .setOnItemChildClickListener(R.id.tv_power_info);
        //用电量显示
        if (bean.getPowerStartTime() == 0) {
            holder.setVisibility(R.id.tv_power_info, View.GONE);
        } else {
            String powerInfo = String.format(Locale.getDefault(),
                    "从%s至今用电量为%.2fkwh",
                    sdf.format(new Date(bean.getPowerStartTime())),
                    bean.getTotalPower() / 1000d);
            holder.setText(R.id.tv_power_info, powerInfo);
        }
        //开关名称及状态
        ArrayList<String> names = bean.getNames();
        if (names != null && names.size() == 5) {
            holder.setText(R.id.tv_name, TextUtils.isEmpty(names.get(0)) ? "插排" : names.get(0))
                    .setText(R.id.tv_1, TextUtils.isEmpty(names.get(1)) ? "1. 总开关" : "1. " + names.get(1))
                    .setText(R.id.tv_2, TextUtils.isEmpty(names.get(2)) ? "2. 开关" : "2. " + names.get(2))
                    .setText(R.id.tv_3, TextUtils.isEmpty(names.get(3)) ? "3. 开关" : "3. " + names.get(3))
                    .setText(R.id.tv_4, TextUtils.isEmpty(names.get(4)) ? "4. 开关" : "4. " + names.get(4));
        }

        String status = bean.getStatus();
        SwitchCompat sb1 = holder.getView(R.id.sb_1);
        SwitchCompat sb2 = holder.getView(R.id.sb_2);
        SwitchCompat sb3 = holder.getView(R.id.sb_3);
        SwitchCompat sb4 = holder.getView(R.id.sb_4);

        sb1.setOnCheckedChangeListener(null);
        sb2.setOnCheckedChangeListener(null);
        sb3.setOnCheckedChangeListener(null);
        sb4.setOnCheckedChangeListener(null);

        sb1.setChecked(status.charAt(0) == '1');
        sb2.setChecked(status.charAt(1) == '1');
        sb3.setChecked(status.charAt(2) == '1');
        sb4.setChecked(status.charAt(3) == '1');

        CompoundButton.OnCheckedChangeListener changeListener = (buttonView, isChecked) -> {
            int pos;
            switch (buttonView.getId()) {
                case R.id.sb_1: {
                    pos = 0;
                    break;
                }
                case R.id.sb_2: {
                    pos = 1;
                    break;
                }
                case R.id.sb_3: {
                    pos = 2;
                    break;
                }
                case R.id.sb_4:
                default: {
                    pos = 3;
                }
            }
            int adapterPosition = holder.getAdapterPosition();
            Dc1Bean dc1Bean = getData().get(adapterPosition);
            String s = dc1Bean.getStatus();
            StringBuilder sb = new StringBuilder(s);
            sb.replace(pos, pos + 1, isChecked ? "1" : "0");
            if (isChecked && pos != 0) {
                sb.replace(0, 1, "1");
            }
            dc1Bean.setStatus(sb.toString());
            ConnectApi.switchDc1Status(dc1Bean.getId(), sb.toString());
        };

        sb1.setOnCheckedChangeListener(changeListener);
        sb2.setOnCheckedChangeListener(changeListener);
        sb3.setOnCheckedChangeListener(changeListener);
        sb4.setOnCheckedChangeListener(changeListener);
    }
}
