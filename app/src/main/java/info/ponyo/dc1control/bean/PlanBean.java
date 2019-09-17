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

    public static final String DAY_MONDAY = "1";
    public static final String DAY_TUESDAY = "2";
    public static final String DAY_WEDNESDAY = "3";
    public static final String DAY_THURSDAY = "4";
    public static final String DAY_FRIDAY = "5";
    public static final String DAY_SATURDAY = "6";
    public static final String DAY_SUNDAY = "7";

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
     * 设备开关指令
     */
    private String status;

    /**
     * 触发时间,
     * 格式 05:43:22
     */
    private String triggerTime;

    /**
     * 重复状态，一次，每天，
     */
    private String repeat;

    /**
     * 是否过期,重复一次的任务
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
}
