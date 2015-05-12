package pt.utl.ist.airdesk.airdesk.datastructures;

import java.io.Serializable;

/**
 * Created by pedro on 12-05-2015.
 */
public class DeviceInformation implements Serializable {

    String deviceName;
    String userLogin;
    String ip;
    int port;

    public DeviceInformation(int port, String deviceName, String userLogin, String ip) {
        this.port = port;
        this.deviceName = deviceName;
        this.userLogin = userLogin;
        this.ip = ip;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
