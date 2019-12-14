package info.ponyo.dc1control.bean;

/**
 * @author zxq
 * @Date 2019/12/14.
 * @Description:
 */
public class HostBean {
    private String host;
    private String socketPort;
    private String httpPort;
    private String token;

    public String getHost() {
        return host;
    }

    public HostBean setHost(String host) {
        this.host = host;
        return this;
    }

    public String getSocketPort() {
        return socketPort;
    }

    public HostBean setSocketPort(String socketPort) {
        this.socketPort = socketPort;
        return this;
    }

    public String getHttpPort() {
        return httpPort;
    }

    public HostBean setHttpPort(String httpPort) {
        this.httpPort = httpPort;
        return this;
    }

    public String getToken() {
        return token;
    }

    public HostBean setToken(String token) {
        this.token = token;
        return this;
    }
}
