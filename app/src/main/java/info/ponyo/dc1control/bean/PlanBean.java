package info.ponyo.dc1control.bean;


import android.text.TextUtils;

import java.util.ArrayList;

public class PlanBean {

    public static ArrayList<String> WEEK_DAY_CN = new ArrayList<>();

    static {
        WEEK_DAY_CN.add("周一");
        WEEK_DAY_CN.add("周二");
        WEEK_DAY_CN.add("周三");
        WEEK_DAY_CN.add("周四");
        WEEK_DAY_CN.add("周五");
        WEEK_DAY_CN.add("周六");
        WEEK_DAY_CN.add("周日");
    }

    public static final String REPEAT_ONCE = "repeat_once";
    public static final String REPEAT_EVERYDAY = "repeat_everyday";
    public static final String REPEAT_AT_FIXED_RATE = "repeat_at_fixed_rate";

    public static final String DAY_MONDAY = "1";
    public static final String DAY_TUESDAY = "2";
    public static final String DAY_WEDNESDAY = "3";
    public static final String DAY_THURSDAY = "4";
    public static final String DAY_FRIDAY = "5";
    public static final String DAY_SATURDAY = "6";
    public static final String DAY_SUNDAY = "7";

    public static final String SWITCH_INDEX_MAIN = "0";
    public static final String SWITCH_INDEX_FIRST = "1";
    public static final String SWITCH_INDEX_SECOND = "2";
    public static final String SWITCH_INDEX_THIRD = "3";

    /**
     * uuid
     */
    private String id;

    /**
     * 触发的设备Id
     */
    private String deviceId;

    /**
     * 触发的设备名称
     */
    private String deviceName;

    /**
     * 任务最新修改或添加时间
     */
    private long updateTime;

    /**
     * 设备开关指令(例如：1101)
     */
    private String status;

    /**
     * 设备单控指令，例如：1开，0关
     */
    private String command;

    /**
     * 设备单控指令:控制开关对象
     *
     * @see #SWITCH_INDEX_MAIN
     * @see #SWITCH_INDEX_FIRST
     * @see #SWITCH_INDEX_SECOND
     * @see #SWITCH_INDEX_THIRD
     */
    private String switchIndex;

    /**
     * 触发时间,
     * 格式 05:43:22
     */
    private String triggerTime;

    /**
     * 重复状态:一次/每天/星期自定义[1,2,3,4]/定时周期性重复
     */
    private String repeat;

    /**
     * 定时周期性重复时，存储的数据:"30,5"(每30分钟执行一次，每次5分钟)
     */
    private String repeatData;

    /**
     * 是否开启,开启/关闭控制
     */
    private boolean enable;


    public String getId() {
        return id;
    }

    public PlanBean setId(String id) {
        this.id = id;
        return this;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public PlanBean setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public PlanBean setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        return this;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public PlanBean setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public PlanBean setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getTriggerTime() {
        return triggerTime;
    }

    public PlanBean setTriggerTime(String triggerTime) {
        this.triggerTime = triggerTime;
        return this;
    }

    public String getRepeat() {
        return repeat;
    }

    public String getRepeat_2showstr() {
        if (TextUtils.isEmpty(repeat) || REPEAT_EVERYDAY.equals(repeat)) {
            return "每天";
        }
        if (REPEAT_ONCE.equals(repeat)) {
            return "一次";
        }
        if (REPEAT_AT_FIXED_RATE.equals(repeat)) {
            String[] split = repeatData.split(",");
            if (split.length != 2) {
                return "周期异常";
            }
            return String.format("每%s分钟执行一次，每次%s分钟", split[0], split[1]);
        }
        String[] split = repeat.split(",");
        StringBuilder sb = new StringBuilder("每");
        for (int i = 0; i < split.length; i++) {
            try {
                int anInt = Integer.parseInt(split[i]);
                String weekDay = WEEK_DAY_CN.get(anInt - 1);
                sb.append(weekDay).append("，");
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        if (sb.charAt(sb.length() - 1) == '，') {
            return sb.substring(0, sb.length() - 1);
        }
        return sb.toString();
    }

    public PlanBean setRepeat(String repeat) {
        this.repeat = repeat;
        return this;
    }

    public boolean isEnable() {
        return enable;
    }

    public PlanBean setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public String getRepeatData() {
        return repeatData;
    }

    /**
     * 定时周期性重复时，存储的数据:"30,5"(每30分钟执行一次，每次5分钟)
     */
    public PlanBean setRepeatData(String repeatData) {
        this.repeatData = repeatData;
        return this;
    }

    public String getCommand() {
        return command;
    }

    public PlanBean setCommand(String command) {
        this.command = command;
        return this;
    }

    public String getSwitchIndex() {
        return switchIndex;
    }

    public PlanBean setSwitchIndex(String switchIndex) {
        this.switchIndex = switchIndex;
        return this;
    }
}
