package info.ponyo.dc1control.bean;


import android.text.TextUtils;

public class PlanBean {

    public static final String REPEAT_ONCE = "repeat_once";
    public static final String REPEAT_EVERYDAY = "repeat_everyday";

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
        return TextUtils.isEmpty(repeat) || repeat.equals(REPEAT_EVERYDAY) ? "每天" : "一次";
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
