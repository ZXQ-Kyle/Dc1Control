package info.ponyo.dc1control.bean;

import java.util.ArrayList;

/**
 * @author zxq
 */
public class Dc1Bean {
    /**
     * 唯一id
     */
    private String id;
    /**
     * 实际状态
     * 1111
     */
    private String status;
    private int I;
    private int V;
    private int P;
    private long updateTime;
    private boolean online;

    /**
     * 开始计算用电量的起始时间
     */
    private long powerStartTime;
    private long totalPower;

    /**
     * 插排名称，1-4开关名称
     */
    private ArrayList<String> names;

    public String getId() {
        return id;
    }

    public Dc1Bean setId(String mac) {
        this.id = mac;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public Dc1Bean setStatus(String status) {
        this.status = status;
        return this;
    }

    public int getI() {
        return I;
    }

    public Dc1Bean setI(int i) {
        I = i;
        return this;
    }

    public int getV() {
        return V;
    }

    public Dc1Bean setV(int v) {
        V = v;
        return this;
    }

    public int getP() {
        return P;
    }

    public Dc1Bean setP(int p) {
        P = p;
        return this;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public Dc1Bean setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public ArrayList<String> getNames() {
        return names == null ? new ArrayList<>(5) : names;
    }

    public Dc1Bean setNames(ArrayList<String> names) {
        this.names = names;
        return this;
    }

    public boolean isOnline() {
        return online;
    }

    public Dc1Bean setOnline(boolean online) {
        this.online = online;
        return this;
    }

    public long getPowerStartTime() {
        return powerStartTime;
    }

    public Dc1Bean setPowerStartTime(long powerStartTime) {
        this.powerStartTime = powerStartTime;
        return this;
    }

    public long getTotalPower() {
        return totalPower;
    }

    public Dc1Bean setTotalPower(long totalPower) {
        this.totalPower = totalPower;
        return this;
    }

    @Override
    public String toString() {
        return "Dc1Bean{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", I=" + I +
                ", V=" + V +
                ", P=" + P +
                ", updateTime=" + updateTime +
                '}';
    }
}
