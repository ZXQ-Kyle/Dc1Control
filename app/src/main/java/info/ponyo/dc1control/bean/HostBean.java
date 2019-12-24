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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        HostBean hostBean = (HostBean) o;

        if (host != null ? !host.equals(hostBean.host) : hostBean.host != null) {
            return false;
        }
        if (socketPort != null ? !socketPort.equals(hostBean.socketPort) : hostBean.socketPort != null) {
            return false;
        }
        if (httpPort != null ? !httpPort.equals(hostBean.httpPort) : hostBean.httpPort != null) {
            return false;
        }
        return token != null ? token.equals(hostBean.token) : hostBean.token == null;
    }

    @Override
    public int hashCode() {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + (socketPort != null ? socketPort.hashCode() : 0);
        result = 31 * result + (httpPort != null ? httpPort.hashCode() : 0);
        result = 31 * result + (token != null ? token.hashCode() : 0);
        return result;
    }
}
